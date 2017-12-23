package com.securemypasswords.securemypasswords.passwordListActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.securemypasswords.securemypasswords.AppRequestVariables;
import com.securemypasswords.securemypasswords.PasswordManage;
import com.securemypasswords.securemypasswords.R;
import com.securemypasswords.securemypasswords.passwordsStorage.AppElements;
import com.securemypasswords.securemypasswords.passwordsStorage.Group;
import com.securemypasswords.securemypasswords.passwordsStorage.Password;

import java.util.List;

/**
 * Created by amosar on 09/11/17.
 */

public class PasswordListAdaptater extends RecyclerView.Adapter<PasswordListAdaptater.MyViewHolder> {

    private List<AppElements> elements;
    protected PasswordListActivity passwordListActivity;

    public PasswordListAdaptater(List<AppElements> elements, PasswordListActivity activity){
        this.elements = elements;
        passwordListActivity = activity;
    }

    @Override
    public PasswordListAdaptater.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_password_list,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PasswordListAdaptater.MyViewHolder holder, final int position) {
        final AppElements appElements = elements.get(position);
        if(appElements instanceof Password) {
            holder.getTypeImage().setImageResource(R.drawable.ic_unlock);
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    passwordListActivity.runManagePasswordActivity(appElements,position);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener(){

                @Override
                public boolean onLongClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(passwordListActivity);
                    builder.setTitle("Do you want to remove it?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            elements.remove(position);
                            passwordListActivity.updateElements();
                        }
                    });
                    builder.setNegativeButton("Nop", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
                    return true;
                }
            });
        }else if(appElements instanceof Group){
            holder.getTypeImage().setImageResource(R.drawable.ic_folder_star);
            holder.itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Intent intent = new Intent(passwordListActivity, PasswordListActivity.class);
                    intent.putExtra("group",appElements);
                }
            });
        }
        holder.getTitle().setText(appElements.getName());

    }

    @Override
    public int getItemCount() {
        return elements.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView typeImage;
        private TextView title;
        private Object objLocalStorage ;

        public MyViewHolder(View view) {
            super(view);
            typeImage = view.findViewById(R.id.iv_passwordListElement_typeImage);
            title = view.findViewById(R.id.tv_passwordListElement_title);
        }

        public ImageView getTypeImage() {
            return typeImage;
        }

        public void setTypeImage(ImageView typeImage) {
            this.typeImage = typeImage;
        }

        public TextView getTitle() {
            return title;
        }

        public void setTitle(TextView title) {
            this.title = title;
        }

        public Object getObjLocalStorage() {
            return objLocalStorage;
        }

        public void setObjLocalStorage(Object objLocalStorage) {
            this.objLocalStorage = objLocalStorage;
        }
    }
}

