package com.midas.githubstars.ui.frag;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.midas.githubstars.MyApp;
import com.midas.githubstars.R;
import com.midas.githubstars.structure.json.function.ReqBase;
import com.midas.githubstars.structure.json.function.get_user_list.res_get_user_list;
import com.midas.githubstars.ui.adapter.UserAdapter;
import com.midas.githubstars.util.NetworkCtrl;

/*

 */
public class FrApi extends Fragment implements SwipeRefreshLayout.OnRefreshListener
{
    /********************************** Define **********************************/
    /********************************** Member **********************************/
    public MyApp m_App = null;
    public Context m_Context = null;
    public Activity m_Activity = null;
    public RequestManager m_RequestManager = null;
    public res_get_user_list m_ResGetUserList = null;

    public UserAdapter m_Adapter = null;
    public String m_strSearchText = null;//검색어
    public int m_nPage = 1;//start paging number
    public boolean m_bRunning = false;
    /********************************** Controller **********************************/
    private View m_View = null;
    private SwipeRefreshLayout m_ly_SwipeRefresh = null;
    private ListView m_ListView = null;
    private TextView m_tv_TotalCnt = null;
    private EditText m_edit_Search = null;
    private Button m_btn_Search = null;
    /********************************** System Function **********************************/
    //-----------------------------------------------------------------------------------
    //
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        m_Context = getActivity();
        m_Activity = getActivity();
        m_App = (MyApp)getActivity().getApplication();
        if(m_App.m_bInit == false)
            m_App.init(m_Context);

        m_RequestManager = Glide.with(m_Context);
    }

    //-----------------------------------------------------------------------------------
    //
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        m_View = (View) inflater.inflate(R.layout.frag_api, container, false);
        initValue();
        setInitLayout(m_View);
        return m_View;
    }
    //-----------------------------------------------------------------------------------
    //
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);


    }
    //-----------------------------------------------------------------------------------
    //
    @Override
    public void onStop()
    {
        super.onStop();
        m_App.HideLoadingDlg();
    }

    /********************************** User Function **********************************/
    //-----------------------------------------------------------------------------------
    //
    public FrApi()
    {

    }

    //-----------------------------------------------------------------------------------
    //
    public void initValue()
    {
        //m_Handler = new Handler(Looper.getMainLooper());
    }
    //-----------------------------------------------------------------------------------
    //
    public void setInitLayout(View pView)
    {
        m_ly_SwipeRefresh = (SwipeRefreshLayout)pView.findViewById(R.id.ly_SwipeRefresh);
        m_ListView = (ListView)pView.findViewById(R.id.listView);
        m_tv_TotalCnt = (TextView)pView.findViewById(R.id.tv_TotalCnt);
        m_edit_Search = (EditText)pView.findViewById(R.id.edit_Search);
        m_btn_Search = (Button)pView.findViewById(R.id.btn_Search);


        //evnet..
        m_ly_SwipeRefresh.setOnRefreshListener(this);
        m_btn_Search.setOnClickListener(onClickGoSearch);

        settingView();
    }
    //---------------------------------------------------------------------------------------------------
    //
    public void settingView()
    {

    }
    //---------------------------------------------------------------------------------------------------
    //
    public void getUserListFromGithub(final String q, final int page, final int per_page)
    {
        if(m_App.m_NetworkCtrl.getStatus() == NetworkCtrl.STAT_NOT_CONNECTED)//Network체크
        {
            Toast.makeText(m_Context, m_Context.getResources().getString(R.string.msg_network_not_connected), Toast.LENGTH_SHORT).show();
            return;
        }
        else
        {
            if(m_bRunning == false)
            {
                m_bRunning = true;
                m_App.ShowLoadingDlg(m_Context);
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        m_bRunning = true;
                        m_ResGetUserList = m_App.m_WebReqCtrl.get_user_list(q, page, per_page);

                        if(m_ResGetUserList != null)//success
                        {
                            ((Activity)m_Context).runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    m_bRunning = false;
                                    m_App.HideLoadingDlg();

                                    m_nPage++;
                                    settingUserListView(m_ResGetUserList);
                                }
                            });
                        }
                        else//fail
                        {
                            ((Activity)m_Context).runOnUiThread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    m_bRunning = false;
                                    m_App.HideLoadingDlg();
                                    return;
                                }
                            });
                        }
                    }
                }).start();
            }
        }
    }
    //---------------------------------------------------------------------------------------------------
    //
    public void settingUserListView(res_get_user_list pRes)
    {
        if(pRes == null)
            return;

        if(pRes.items == null)
            return;


        if(pRes.total_count != null)
            m_tv_TotalCnt.setText(String.format("totla count : %s", pRes.total_count));

        if(m_Adapter == null)
        {
            m_Adapter = new UserAdapter(m_Context, m_RequestManager, 0, pRes.items, m_App);
            m_ListView.setAdapter(m_Adapter);
            m_ListView.setOnScrollListener(onScrollListener);
        }
        else
        {
            m_Adapter.addData(pRes.items);
            m_Adapter.notifyDataSetChanged();
        }
    }
    //---------------------------------------------------------------------------------------------------
    //
    public boolean checkValidation()
    {
        boolean bResult = false;
        m_strSearchText = m_edit_Search.getText().toString().trim();

        if(m_strSearchText == null)
        {
            m_App.alertDialog(m_Context, null, m_Context.getResources().getString(R.string.str_msg_1), "확인", null);
            bResult = false;
        }
        else if(m_strSearchText.equals("") || m_strSearchText.length() == 0)
        {
            m_App.alertDialog(m_Context, null, m_Context.getResources().getString(R.string.str_msg_1), "확인", null);
            bResult = false;
        }
        else
        {
            bResult = true;
        }
        return bResult;
    }

    //---------------------------------------------------------------------------------------------------
    //refresh
    public void setRefresh()
    {
        m_nPage = 1;
        m_Adapter = null;
        m_ly_SwipeRefresh.setRefreshing(false);
        getUserListFromGithub(m_strSearchText, m_nPage, ReqBase.ITEM_COUNT);
    }
    /********************************** Listener **********************************/
    //------------------------------------------------------------------------------------
    //
    View.OnClickListener onClickGoSearch = new View.OnClickListener()
    {
        @Override
        public void onClick(View view)
        {
            if(checkValidation())
            {
                m_nPage = 1;
                if(m_Adapter != null)
                    m_Adapter.clear();

                getUserListFromGithub(m_strSearchText, m_nPage, ReqBase.ITEM_COUNT);
            }
            else
            {

            }
        }
    };
    //------------------------------------------------------------------------------------
    //
    AbsListView.OnScrollListener onScrollListener = new AbsListView.OnScrollListener()
    {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState)
        {
            View v = m_ListView.getChildAt(0);
            int offset = (v == null) ? 0 : v.getTop();

            if(offset == 0)//top
            {

            }
            else
            {

            }
        }


        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
        {
            if(firstVisibleItem + visibleItemCount >= totalItemCount)//Bottom
            {
                if(m_ResGetUserList != null)
                {
                    if(m_ResGetUserList.items != null)
                    {
                        if(m_ResGetUserList.items.size() >= ReqBase.ITEM_COUNT)   //더보기..
                        {
                            getUserListFromGithub(m_strSearchText, m_nPage, ReqBase.ITEM_COUNT);
                        }
                        else
                        {
                            return;
                        }

                    }
                }
            }
        }
    };

    //------------------------------------------------------------------------------------
    //SwipeRefreshLayout.OnRefreshListener
    @Override
    public void onRefresh()
    {
        setRefresh();
    }

    /********************************** Interface **********************************/
    //------------------------------------------------------------------------------------
    //public interface IfCallback
}
