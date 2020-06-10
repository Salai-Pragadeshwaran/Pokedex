package com.example.pokedex2.ui.main;

import android.content.Context;
import android.graphics.Canvas;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pokedex2.MainActivity;
import com.example.pokedex2.R;
import com.example.pokedex2.database.FavPokemon;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<Pokemon>> {

    public static String publicPokeURL = null;
    public static String publicFromFragment = null;

    private MainViewModel mViewModel;
    LoaderManager loaderManager;
    RecyclerView pokeList;

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    PokemonAdapter pokemonAdapter;
    ArrayList<Pokemon> pokemons;
    public String URL_POKEAPI = "https://pokeapi.co/api/v2/pokemon?limit=20&offset=0";//+ 20*(mViewModel.pageNo-1);
    TextView mEmptyStateTextView;
    View loadingIndicator;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.pokemon_fragment, container, false);

        pokemons = new ArrayList<>();
        pokemonAdapter = new PokemonAdapter(pokemons, getContext());
        mEmptyStateTextView = (TextView) root.findViewById(R.id.empty_view);
        loadingIndicator = root.findViewById(R.id.progress_bar);

        pokeList = (RecyclerView) root.findViewById(R.id.pokemon_Recycler);
        pokeList.setAdapter(pokemonAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        pokeList.setLayoutManager(linearLayoutManager);


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


        TextView previous = (TextView) root.findViewById(R.id.prevPage);
        TextView next = (TextView) root.findViewById(R.id.nextPage);
        final TextView pageNo = (TextView) root.findViewById(R.id.pageNo);
        Button search = (Button) root.findViewById(R.id.startSearch);
        final EditText searchText = (EditText) root.findViewById(R.id.searchPokemon);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchQuery = searchText.getText().toString();
                searchQuery = searchQuery.toLowerCase();

                if (searchQuery != "") {
                    URL_POKEAPI = "https://pokeapi.co/api/v2/pokemon/" + searchQuery;
                } else {
                    URL_POKEAPI = "https://pokeapi.co/api/v2/pokemon?limit=20&offset=0" + 20 * (mViewModel.pageNo - 1);
                }

                loaderManager.destroyLoader(0);
                loadingIndicator.setVisibility(View.VISIBLE);
                mEmptyStateTextView.setVisibility(View.GONE);
                loaderManager.initLoader(0, null, MainFragment.this);
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((mViewModel.pageNo > 1) && (publicPokeURL == null)) {
                    mViewModel.pageNo--;
                    String text = String.valueOf(mViewModel.pageNo);
                    text = "Page " + text;
                    pageNo.setText(text);

                    URL_POKEAPI = "https://pokeapi.co/api/v2/pokemon?limit=20&offset=0" + 20 * (mViewModel.pageNo - 1);

                    loaderManager.destroyLoader(0);
                    loadingIndicator.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setVisibility(View.GONE);
                    loaderManager.initLoader(0, null, MainFragment.this);
                }
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (publicPokeURL == null) {
                    mViewModel.pageNo++;
                    String text = String.valueOf(mViewModel.pageNo);
                    text = "Page " + text;
                    pageNo.setText(text);

                    URL_POKEAPI = "https://pokeapi.co/api/v2/pokemon?limit=20&offset=0" + 20 * (mViewModel.pageNo - 1);

                    loaderManager.destroyLoader(0);
                    loadingIndicator.setVisibility(View.VISIBLE);
                    mEmptyStateTextView.setVisibility(View.GONE);
                    loaderManager.initLoader(0, null, MainFragment.this);
                }
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(pokeList);

        return root;
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            if (direction == ItemTouchHelper.RIGHT) {
                Pokemon swipedPokemon = pokemons.get(position);
                pokemons.remove(position);
                pokemonAdapter.notifyItemRemoved(position);
                FavPokemon favPokemon = new FavPokemon();
                favPokemon.setName(swipedPokemon.getName());
                favPokemon.setImageUrl(swipedPokemon.getImgUrl());
                favPokemon.setType(null);
                pokemons.add(position, swipedPokemon);
                pokemonAdapter.notifyItemInserted(position);
                List<FavPokemon> favPokemons = MainActivity.pokedexDatabase.pokeDao().getFavPokemon();
                for (int i = 0; i < favPokemons.size(); i++) {
                    if (favPokemons.get(i).getName().equals(swipedPokemon.getName())) {
                        Toast.makeText(getContext(), "Already a Favorite", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                MainActivity.pokedexDatabase.pokeDao().addPokemonDetail(favPokemon);
                Toast.makeText(getContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                    .addBackgroundColor(ContextCompat.getColor(getContext(), R.color.bgColorImg))
                    .addActionIcon(R.drawable.ic_favorite_red_35dp)
                    .create()
                    .decorate();
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        // TODO: Use the ViewModel
    }

    @NonNull
    @Override
    public Loader<List<Pokemon>> onCreateLoader(int id, @Nullable Bundle args) {
        if (publicPokeURL != null) {
            return new PokemonLoader(getContext(), publicPokeURL);
        }
        return new PokemonLoader(getContext(), URL_POKEAPI);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Pokemon>> loader, List<Pokemon> data) {
        mEmptyStateTextView.setText(R.string.no_pokemons);
        loadingIndicator.setVisibility(View.GONE);
        pokemons.clear();
        pokeList.removeAllViewsInLayout();
        pokeList.setAdapter(pokemonAdapter);
        pokemons.addAll(data);
        if (pokemons.size() != 0) {
            mEmptyStateTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Pokemon>> loader) {
        pokemons.clear();
    }


}
