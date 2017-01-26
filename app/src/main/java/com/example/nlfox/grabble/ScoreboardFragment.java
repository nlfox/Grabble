package com.example.nlfox.grabble;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ScoreboardFragment extends Fragment {

    private int mColumnCount = 1;

    private static final String ARG_COLUMN_COUNT = "column-count";

    private OnListFragmentInteractionListener mListener;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ScoreboardFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }
    // Async task for refreshing the scoreboard
    private class RefreshTask extends AsyncTask<Object, Void, Boolean> {
        protected Boolean doInBackground(Object... params) {
            try {
                GrabbleApplication.getAppContext(getActivity().getApplication()).updateScoreboard();
                getActivity().runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                recyclerView.setAdapter(new MyItemRecyclerViewAdapter(GrabbleApplication.getAppContext(getActivity().getApplicationContext()).scoreItems, mListener));
                            }
                        }
                );
            } catch (Exception e) {
                //retry when network error
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utils.buildAlertBox(getActivity(), () -> new RefreshTask().execute()).show();
                    }
                });
                return false;
            }
            return true;
        }


        @Override
        protected void onPostExecute(Boolean result) {
            //stop animation
            mSwipeRefreshLayout.setRefreshing(false);
        }

    }

    SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_score_board, container, false);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view;
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Refresh items
                try {
                    new RefreshTask().execute();
                } catch (Exception e) {
                    Utils.buildAlertBox(getActivity(), () -> {
                    });
                }
            }

        });
        // Set the adapter
        if (view.findViewById(R.id.list) instanceof RecyclerView) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new MyItemRecyclerViewAdapter(GrabbleApplication.getAppContext(getActivity().getApplicationContext()).scoreItems, mListener));
        }
        return view;
    }

    RecyclerView recyclerView;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(ScoreboardContent.ScoreItem item);
    }


}
