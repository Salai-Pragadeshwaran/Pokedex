package com.example.pokedex2.ui.items;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedex2.R;

import java.util.ArrayList;
import java.util.List;

public class ItemFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Item>> {

    private ItemViewModel mViewModel;
    LoaderManager loaderManager;
    RecyclerView itemList;
    ItemAdapter itemAdapter;
    ArrayList<Item> items;
    public String URL_POKEAPI = "https://pokeapi.co/api/v2/item/?limit=20&offset=0";//+ 20*(mViewModel.pageNo-1);
    TextView mEmptyStateTextView;
    View loadingIndicator;


    public static ItemFragment newInstance() {
        return new ItemFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.item_fragment, container, false);

        items = new ArrayList<>();
        itemAdapter = new ItemAdapter(items, getContext());
        mEmptyStateTextView = (TextView) root.findViewById(R.id.empty_view_item);
        loadingIndicator = root.findViewById(R.id.progress_bar_item);

        itemList = (RecyclerView) root.findViewById(R.id.item_Recycler);
        itemList.setAdapter(itemAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        itemList.setLayoutManager(linearLayoutManager);


        ConnectivityManager connMgr = (ConnectivityManager)
                this.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        loaderManager = getLoaderManager();
        if (networkInfo != null && networkInfo.isConnected()) {
            loaderManager.initLoader(0, null, this);
        } else {
            loadingIndicator.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }


        TextView previous = (TextView) root.findViewById(R.id.prevPage_item);
        TextView next = (TextView) root.findViewById(R.id.nextPage_item);
        final TextView pageNo = (TextView) root.findViewById(R.id.pageNo_item);
        Button search = (Button) root.findViewById(R.id.startSearch_item);
        final EditText searchText = (EditText) root.findViewById(R.id.searchItem);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchText.getText().toString();
                searchQuery = searchQuery.toLowerCase();

                if (searchQuery != "") {
                    URL_POKEAPI = "https://pokeapi.co/api/v2/item/" + searchQuery;
                } else {
                    URL_POKEAPI = "https://pokeapi.co/api/v2/item?limit=20&offset=0" + 20 * (mViewModel.pageNo - 1);
                }

                loaderManager.destroyLoader(0);
                loadingIndicator.setVisibility(View.VISIBLE);
                mEmptyStateTextView.setVisibility(View.GONE);
                loaderManager.initLoader(0, null, ItemFragment.this);
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewModel.pageNo > 1) {
                    mViewModel.pageNo--;
                    String text = String.valueOf(mViewModel.pageNo);
                    text = "Page " + text;
                    pageNo.setText(text);

                    URL_POKEAPI = "https://pokeapi.co/api/v2/item?limit=20&offset=0" + 20 * (mViewModel.pageNo - 1);

                    loaderManager.destroyLoader(0);
                    loadingIndicator.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setVisibility(View.GONE);
                    loaderManager.initLoader(0, null, ItemFragment.this);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.pageNo++;
                String text = String.valueOf(mViewModel.pageNo);
                text = "Page " + text;
                pageNo.setText(text);

                URL_POKEAPI = "https://pokeapi.co/api/v2/item?limit=20&offset=0" + 20 * (mViewModel.pageNo - 1);

                loaderManager.destroyLoader(0);
                loadingIndicator.setVisibility(View.VISIBLE);
                mEmptyStateTextView.setVisibility(View.GONE);
                loaderManager.initLoader(0, null, ItemFragment.this);
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ItemViewModel.class);
        // TODO: Use the ViewModel
    }

    @NonNull
    @Override
    public Loader<List<Item>> onCreateLoader(int id, @Nullable Bundle args) {
        return new ItemLoader(getContext(), URL_POKEAPI);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Item>> loader, List<Item> data) {
        mEmptyStateTextView.setText(R.string.no_items);
        loadingIndicator.setVisibility(View.GONE);
        items.clear();
        itemList.removeAllViewsInLayout();
        itemList.setAdapter(itemAdapter);
        items.addAll(data);
        if (items.size() != 0) {
            mEmptyStateTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Item>> loader) {
        items.clear();
    }
}
