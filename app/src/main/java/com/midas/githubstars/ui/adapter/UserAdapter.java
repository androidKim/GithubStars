package com.midas.githubstars.ui.adapter;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.midas.githubstars.MyApp;
import com.midas.githubstars.R;
import com.midas.githubstars.structure.json.core.user;
import com.midas.githubstars.structure.json.db.tbUser;
import com.midas.githubstars.util.BitmapUtil;

import java.util.ArrayList;

import jp.wasabeef.glide.transformations.CropCircleTransformation;


/**
 * User ListAdapter
 */

public class UserAdapter extends ArrayAdapter
{
    /****************************** Define ******************************/
    /****************************** Member ******************************/
    private MyApp m_App = null;
    private Context m_Context = null;
    private LayoutInflater m_LayoutInflater = null;
    private RequestManager m_RequestManager = null;
    private UserAdapter m_This = null;
    private ArrayList<user> m_Items = null;
    /****************************** Event ******************************/
    //---------------------------------------------------------------------------------------------------
    //
    public UserAdapter(Context context, RequestManager pRequestManger, int resource, ArrayList<user> objects, MyApp pApp)
    {
        super(context, resource, objects);
        this.m_App = pApp;
        this.m_Context = context;
        this.m_RequestManager = pRequestManger;
        this.m_This = this;
        this.m_Items = objects;
        this.m_LayoutInflater = (LayoutInflater) m_Context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    //---------------------------------------------------------------------------------------------------
    //
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder;
        View pView = convertView;

        // Get Item Data
        user pInfo = m_Items.get(position);

        if( pInfo == null )
            return pView;

        if(pView == null)
        {
            pView = m_LayoutInflater.inflate(R.layout.row_user, parent, false);
            holder = new ViewHolder();
            pView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)pView.getTag();
            holder.init();
        }
        settingRowView(pView, pInfo, holder);

        return pView;
    }
    /****************************** Function ******************************/
    //---------------------------------------------------------------------------------------------------
    // set Contents Data
    public void setData(user pInfo)
    {
        m_Items.add(pInfo);
    }
    //---------------------------------------------------------------------------------------------------
    // AddData
    public void addData(ArrayList<user> array)
    {
        m_Items.addAll(array);
    }

    //---------------------------------------------------------------------------------------------------
    // Refresh UI
    public void RefreshList()
    {
        m_This.notifyDataSetChanged();
    }
    //---------------------------------------------------------------------------------------------------
    //
    public void settingRowView(View pView, user pInfo, ViewHolder holder)
    {
        //null Check
        if(pView==null || pInfo==null)
            return;

        holder.tv_Name = (TextView)pView.findViewById(R.id.tv_Name);
        holder.iv_Profile = (ImageView)pView.findViewById(R.id.iv_Profile);
        holder.iv_Fav = (ImageView)pView.findViewById(R.id.iv_Fav);

        if(pInfo.login != null)
            holder.tv_Name.setText(pInfo.login);

        if(pInfo.avatar_url != null)
        {
            m_RequestManager.load(pInfo.avatar_url).bitmapTransform(new CropCircleTransformation(new BitmapUtil.CustomBitmapPool())).into(holder.iv_Profile);//프로필이미지
        }

        if(pInfo.fav_status != null)
        {
            if(pInfo.fav_status.equals(tbUser.FAV_TRUE))
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    holder.iv_Fav.setBackground(m_Context.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                else
                    holder.iv_Fav.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
            }
            else
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    holder.iv_Fav.setBackground(m_Context.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
                else
                    holder.iv_Fav.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
            }
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                holder.iv_Fav.setBackground(m_Context.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
            else
                holder.iv_Fav.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
        }

        holder.iv_Fav.setTag(pInfo);
        holder.iv_Fav.setOnClickListener(onClickFav);

        //localinfo..
        tbUser pLocalInfo = m_App.m_DbCtrl.getUserInfo(m_App.m_DbCtrl.m_Db, pInfo.id);
        if(pLocalInfo != null)
        {
            if(pLocalInfo.fav_status != null)
            {
                if(pLocalInfo.fav_status.equals(tbUser.FAV_TRUE))
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        holder.iv_Fav.setBackground(m_Context.getResources().getDrawable(R.drawable.ic_favorite_black_24dp));
                    else
                        holder.iv_Fav.setBackgroundResource(R.drawable.ic_favorite_black_24dp);
                }
                else
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                        holder.iv_Fav.setBackground(m_Context.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
                    else
                        holder.iv_Fav.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
                }
            }
        }
        else
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                holder.iv_Fav.setBackground(m_Context.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp));
            else
                holder.iv_Fav.setBackgroundResource(R.drawable.ic_favorite_border_black_24dp);
        }
    }
    /**************************** Listener ****************************/
    //---------------------------------------------------------------------------------------------------
    //
    View.OnClickListener onClickFav = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            user pInfo = (user)view.getTag();
            if(pInfo == null)
                return;

            //api.
            if(pInfo.fav_status != null)
            {
                if(pInfo.fav_status.equals(tbUser.FAV_TRUE))
                    pInfo.fav_status = tbUser.FAV_FALSE;
                else
                    pInfo.fav_status = tbUser.FAV_TRUE;
            }
            else
            {
                pInfo.fav_status = tbUser.FAV_TRUE;
            }

            //local..
            if(pInfo.id != null)
            {
                tbUser pLocalInfo = m_App.m_DbCtrl.getUserInfo(m_App.m_DbCtrl.m_Db, pInfo.id);
                if(pLocalInfo != null)
                {
                    //update
                    if(pLocalInfo.fav_status == null)
                        return;

                    if(pLocalInfo.fav_status.equals(tbUser.FAV_TRUE))
                        pLocalInfo.fav_status = tbUser.FAV_FALSE;
                    else
                        pLocalInfo.fav_status = tbUser.FAV_TRUE;

                    m_App.m_DbCtrl.updateInfo(m_App.m_DbCtrl.m_Db, pLocalInfo);
                }
                else
                {
                    //insert
                    tbUser pInsertInfo = new tbUser(pInfo.login, pInfo.id, pInfo.avatar_url, tbUser.FAV_TRUE);//
                    m_App.m_DbCtrl.insertInfo(m_App.m_DbCtrl.m_Db, pInsertInfo);
                }
            }

            notifyDataSetChanged();
        }
    };
    /**************************** InnerClass ****************************/
    //---------------------------------------------------------------------------------------------------
    //
    static class ViewHolder
    {
        TextView tv_Name;
        ImageView iv_Profile;
        ImageView iv_Fav;

        public void init()
        {
            if (tv_Name != null)
                tv_Name.setText(null);
        }
    }
    /**************************** Listener ****************************/

    /**************************** interface ****************************/
}