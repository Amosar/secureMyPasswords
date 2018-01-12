<?php

require '.././libs/Slim/Http/Request.php';
require_once '../include/DbHandler.php';
require_once '../include/PassHash.php';
require '.././libs/Slim/Slim.php';

\Slim\Slim::registerAutoloader();

$app = new \Slim\Slim();

// User id from db - Global Variable
$user_id = NULL;

/**
 * Adding Middle Layer to authenticate every request
 * Checking if the request has valid api key in the 'authorization' header
 */
function authenticate(\Slim\Route $route) {
    // Getting request headers
    $headers = apache_request_headers();
    $response = array();
    $app = \Slim\Slim::getInstance();

    // Verifying authorization Header
    if (isset($headers['Auth'])) {
        $db = new DbHandler();

        // get the api key
        $api_key = $headers['Auth'];
        // validating api key
        if (!$db->isValidApiKey($api_key)) {
            // api key is not present in users table
            $response["error"] = true;
            $response["message"] = "Access Denied. Invalid Api key";
            echoResponse(401, $response);
            $app->stop();
        } else {
            global $user_id;
            // get user primary key id
            $user_id = $db->getUserId($api_key);
        }
    } else {
        // api key is missing in header
        $response["error"] = true;
        $response["message"] = "Api key is misssing";
        echoResponse(400, $response);
        $app->stop();
    }
}

/**
 * ----------- METHODS WITHOUT AUTHENTICATION ---------------------------------
 */
/**
 * User Registration
 * url - /register
 * method - POST
 * params - email, password
 */

$app->post('/register', function() use ($app) {
            verifyRequiredParams(array('email', 'password'));
            $response = array();

            // reading post params
            $email = $app->request->post('email');
            $password = $app->request->post('password');

            // validating email address
            validateEmail($email);

            $db = new DbHandler();
            $res = $db->createUser($email, $password);

            if ($res == USER_CREATED_SUCCESSFULLY) {
                $response["error"] = false;
                $response["message"] = "You are successfully registered";
            } else if ($res == USER_CREATE_FAILED) {
                $response["error"] = true;
                $response["message"] = "Oops! An error occurred while registering";
            } else if ($res == USER_ALREADY_EXISTED) {
                $response["error"] = true;
                $response["message"] = "Sorry, this email already existed";
            }
            // echo json response
            echoResponse(201, $response);
        });

$app->options('/register', function() use ($app) {
	setGeneriqueHeader();
});

/**
 * User Login
 * url - /login
 * method - POST
 * params - email, password
 */
$app->post('/login', function() use ($app) {

            verifyRequiredParams(array('email', 'password'));

            $email = $app->request()->post('email');
            $password = $app->request()->post('password');
            $response = array();



            $db = new DbHandler();

            if ($db->checkLogin($email, $password)) {
                // get the user by email
                $user = $db->getUserByEmail($email);

                if ($user != NULL) {
                    $response["error"] = false;
                    $response['email'] = $user['email'];
                    $response['apiKey'] = $user['api_key'];
                    $response['registered'] = $user['registered'];

                } else {
                    // unknown error occurred
                    $response['error'] = true;
                    $response['message'] = "An error occurred. Please try again";
                }
            } else {
                // user credentials are wrong
                $response['error'] = true;
                $response['message'] = 'Login failed. Incorrect credentials';
            }

            echoResponse(200,$response);

        });

$app->options('/login', function() use ($app) {
	setGeneriqueHeader();
});

/*
 * ------------------------ METHODS WITH AUTHENTICATION ------------------------
 */

$app->post('/updateuser', 'authenticate', function() use ($app) {
    $data = json_decode($app->request()->getBody(),true);
    // reading post params*
    $email = NULL;
    $password = NULL;

    $response = array();

    if(isset($data['email'])){
        $email = $data['email'];
    }
    if(isset($data['pass'])){
        $password = $data['pass'];
    }

    $db = new DbHandler();
    $headers = apache_request_headers();
    $api_key = $headers['Auth'];

    if ($db->updateUser($email,$password, $api_key)){
        $response['error'] = false;
    } else {
        // user credentials are wrong
        $response['error'] = true;
        $response['message'] = 'An error occurred. Please try again';
    }

    echoResponse(200,$response);

});

$app->options('/updateuser', function() use ($app) {
	setGeneriqueHeader();
});

$app->get('/getPasswordsFile', 'authenticate', function() use ($app) {
    // reading post params*
    $headers = apache_request_headers();

    $api_key = $headers['Auth'];

    $db = new DbHandler();
    $email = $db->getUserEmailByApiKey($api_key);

    $file_path = "../uploads/" . $email;

    if (file_exists($file_path)) {
        header("Content-Transfer-Encoding: binary");
        header("Content-Length: ". filesize($file_path) ."");
        header("Expires: 0");
        header("Cache-Control: no-cache, must-revalidate");


        readfile($file_path);

        $response['error'] = false;
    }else{
        $response['error'] = true;
    }

});

$app->post('/sendPasswordsFile', 'authenticate', function() use ($app) {

    $headers = apache_request_headers();
    $api_key = $headers['Auth'];

    $db = new DbHandler();
    $email = $db->getUserEmailByApiKey($api_key);

    $file_path = "../uploads/" . $email;

    if(move_uploaded_file($_FILES['uploaded_file']['tmp_name'], $file_path)) {
        $response['error'] = false;
    }else{
        $response['error'] = true;
    }

    echoResponse(200,$response);
});

$app->post('/getLastUpdateDateOfPasswordFile', 'authenticate', function() use ($app) {

    $headers = apache_request_headers();
    $api_key = $headers['Auth'];

    $db = new DbHandler();
    $email = $db->getUserEmailByApiKey($api_key);

    $file_path = "../uploads/" . $email;


    if(file_exists($file_path)){
        $response['error'] = false;
        $response['lastUpdate'] = filemtime($file_path);
    }else{
        $response['error'] = true;
        $response['message'] = "no file on server";
    }


    echoResponse(200,$response);
});

/**
 * Verifying required params posted or not
 */
function verifyRequiredParams($required_fields) {
    $error = false;
    $error_fields = "";
    $request_params = array();
    $request_params = $_REQUEST;
    // Handling PUT request params
    if ($_SERVER['REQUEST_METHOD'] == 'PUT') {
        $app = \Slim\Slim::getInstance();
        parse_str($app->request()->getBody(), $request_params);
    }
    foreach ($required_fields as $field) {
        if (!isset($request_params[$field]) || strlen(trim($request_params[$field])) <= 0) {
            $error = true;
            $error_fields .= $field . ', ';
        }
    }

    if ($error) {
        // Required field(s) are missing or empty
        // echo error json and stop the app
        $response = array();
        $app = \Slim\Slim::getInstance();
        $response["error"] = true;
        $response["message"] = 'Required field(s) ' . substr($error_fields, 0, -2) . ' is missing or empty';
        echoResponse(400, $response);
        $app->stop();
    }
}

/**
 * Validating email address
 */
function validateEmail($email) {
    $app = \Slim\Slim::getInstance();
    if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
        $response["error"] = true;
        $response["message"] = 'Email address is not valid';
        echoResponse(400, $response);
        $app->stop();
    }
}

/**
 * Echoing json response to client
 * @param String $status_code Http response code
 * @param Int $response Json response
 */
function echoResponse($status_code, $response) {
    $app = \Slim\Slim::getInstance();
    // Http response code
    $app->status($status_code);

    // setting response content type to json
    $app->controlOrigin('*');
    $app->controlContentType('Content-Type');
    $app->contentType('application/json');

    echo json_encode($response, JSON_UNESCAPED_SLASHES);
}

/**
 * set the header of the request for browser security
 */
function setGeneriqueHeader()
{
    header('Access-Control-Allow-Origin: *');
    header('Access-Control-Allow-Methods: POST, GET, DELETE, PUT, PATCH, OPTIONS');
    header('Access-Control-Allow-Headers: token, Content-Type');
    header('Access-Control-Max-Age: 1728000');
    header('Content-Length: 0');
    header('Content-Type: application/json');
    die();
}

$app->run();
