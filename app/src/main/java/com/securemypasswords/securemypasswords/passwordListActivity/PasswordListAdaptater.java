package com.securemypasswords.securemypasswords.passwordListActivity;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    public PasswordListAdaptater(List<AppElements> elements){
        this.elements = elements;
    }

    @Override
    public PasswordListAdaptater.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_password_list,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PasswordListAdaptater.MyViewHolder holder, int position) {
        AppElements appElements = elements.get(position);
        if(appElements instanceof Password) {
            holder.getTypeImage().setImageResource(R.drawable.ic_unlock);
        }else if(appElements instanceof Group){
            holder.getTypeImage().setImageResource(R.drawable.ic_folder_star);
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

