<?php
/**
 * Class to handle all db operations
 */
class DbHandler {

    private $conn;

    function __construct() {
        require_once dirname(__FILE__) . '/DbConnect.php';
        // opening db connection
        $db = new DbConnect();
        $this->conn = $db->connect();
    }


    /**
     * Creating new user
     * @param String $email User login email id
     * @param String $password User login password
     * @return int Success or type of error
     */
    public function createUser($email, $password) {
        require_once 'PassHash.php';
        // First check if user already existed in db
        if (!$this->isUserExists($email)) {
            // Generating password hash
            $pass = PassHash::hash($password);

            // Generating API key
            $api_key = $this->generateApiKey();

            // insert query
            $stmt = $this->conn->prepare("INSERT INTO secureMyPasswords_users(email, pass, api_key, status) values(?, ?, ?, 1)");
            $stmt->bind_param("sss", $email, $pass, $api_key);

            $result = $stmt->execute();

            $stmt->close();

            // Check for successful insertion
            if ($result) {
                // User successfully inserted
                return USER_CREATED_SUCCESSFULLY;
            } else {
                // Failed to create user
                return USER_CREATE_FAILED;
            }
        } else {
            // User with same email already existed in the db
            return USER_ALREADY_EXISTED;
        }
    }

    /**
     * Checking user login
     * @param String $email User login email id
     * @param String $password User login password
     * @return boolean User login status success/fail
     */
    public function checkLogin($email, $password) {
        // fetching user by email
        $dbPassword = NULL;
        $stmt = $this->conn->prepare("SELECT pass FROM secureMyPasswords_users WHERE email = ?");

        $stmt->bind_param("s", $email);

        $stmt->execute();

        $stmt->bind_result($dbPassword);

        $stmt->store_result();

        if ($stmt->num_rows > 0) {
            // Found user with the email
            // Now verify the password

            $stmt->fetch();

            $stmt->close();

            if (PassHash::check_password($dbPassword, $password)) {
                // User password is correct
                return TRUE;
            } else {
                // user password is incorrect
                return FALSE;
            }
        } else {
            $stmt->close();

            // user not existed with the email
            return FALSE;
        }
    }

    /**
     * Checking for duplicate user by email address
     * @param String $email email to check in db
     * @return boolean :true if user already exist
     */
    private function isUserExists($email) {
        $stmt = $this->conn->prepare("SELECT id from secureMyPasswords_users WHERE email = ?");
        $stmt->bind_param("s", $email);
        $stmt->execute();
        $stmt->store_result();
        $num_rows = $stmt->num_rows;
        $stmt->close();
        return $num_rows > 0;
    }


    /**
     * Fetching user by email
     * @param String $email User email id
     * @return User if found
     */
    public function getUserByEmail($email) {
        $stmt = $this->conn->prepare("SELECT ID, email, api_key, status, registered FROM secureMyPasswords_users WHERE email = ?");
        $stmt->bind_param("s", $email);
        if ($stmt->execute()) {
            // $user = $stmt->get_result()->fetch_assoc();
            $stmt->bind_result($id, $email, $api_key, $status, $registered);
            $stmt->fetch();
            $user = array();
            $user["id"] = $id;
            $user["email"] = $email;
            $user["api_key"] = $api_key;
            $user["status"] = $status;
            $user["registered"] = $registered;
            $stmt->close();
            return $user;
        } else {
            return NULL;
        }
    }


    /**
     * Fetching user id by api key
     * @param String $api_key user api key
     * @return $user_id
     */
    public function getUserId($api_key) {
        $stmt = $this->conn->prepare("SELECT ID FROM secureMyPasswords_users WHERE api_key = ?");
        $stmt->bind_param("s", $api_key);
        if ($stmt->execute()) {
            $stmt->bind_result($user_id);
            $stmt->fetch();
            $stmt->close();
            return $user_id;
        } else {
            return NULL;
        }
    }

    /**
     * Fetching user email by api key
     * @param String $api_key user api key
     * @return $user_Email
     */
    public function getUserEmailByApiKey($api_key) {
        $stmt = $this->conn->prepare("SELECT email FROM secureMyPasswords_users WHERE api_key = ?");
        $stmt->bind_param("s", $api_key);
        if ($stmt->execute()) {
            $stmt->bind_result($user_email);
            $stmt->fetch();
            $stmt->close();
            return $user_email;
        } else {
            return NULL;
        }
    }

    /**
     * Validating user api key
     * If the api key is there in db, it is a valid key
     * @param String $api_key user api key
     * @return boolean : true is key is valid
     */
    public function isValidApiKey($api_key) {
        $stmt = $this->conn->prepare("SELECT ID from secureMyPasswords_users WHERE api_key = ?");
        $stmt->bind_param("s", $api_key);
        $stmt->execute();
        $stmt->store_result();
        $num_rows = $stmt->num_rows;
        $stmt->close();
        return $num_rows > 0;
    }

    /**
         * Updating user information
         * @param String $email is user email
         * @param String $password is user pass
         * @param String $api_key is corresponding key to user
         * @return boolean : True if any of the arguments is incorrect
         */
    public function updateUser($email, $password, $api_key){
        require_once 'PassHash.php';
        if($password != NULL){
            $pass = PassHash::hash($password);
            $stmt = $this->conn->prepare("UPDATE secureMyPasswords_users SET pass=? where api_key = ?");
            $stmt->bind_param('ss',$pass, $api_key);
            $stmt->execute();
            $stmt->close();
        }
        if($email != NULL){
            $stmt = $this->conn->prepare("UPDATE secureMyPasswords_users SET email=? where api_key = ?");
            $stmt->bind_param('ss',$email, $api_key);
            $stmt->execute();
            $stmt->close();
        }
        if($password != NULL and $email != NULL){
         return TRUE;
         }else{
         return FALSE;
         }
    }

      /**
     * Generating random Unique MD5 String for user Api key
     */
    private function generateApiKey() {
        return md5(uniqid(rand(), true));
    }
}
