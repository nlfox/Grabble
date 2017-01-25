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

import java.io.IOException;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ScoreboardFragment extends Fragment {

    // TODO: Customize parameters
    private int mColumnCount = 1;

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";

    private OnListFragmentInteractionListener mListener;

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ScoreboardFragment newInstance(int columnCount) {
        ScoreboardFragment fragment = new ScoreboardFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(ScoreboardContent.ScoreItem item);
    }


    public class ListListener implements OnListFragmentInteractionListener {

        @Override
        public void onListFragmentInteraction(ScoreboardContent.ScoreItem item) {
            return;
        }
    }
}
