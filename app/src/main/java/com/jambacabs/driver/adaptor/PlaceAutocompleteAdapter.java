package com.jambacabs.driver.adaptor;

import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.jambacabs.driver.R;
import com.jambacabs.driver.callbacks.IPlaces;
import com.jambacabs.driver.font.CustomTextView;
import com.jambacabs.driver.singleton.UserSession;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PlaceAutocompleteAdapter extends RecyclerView.Adapter<PlaceAutocompleteAdapter.PlaceViewHolder> implements Filterable {

    /*private ArrayList<JSONObject> mResultList;
    private Context mContext;
    private final PlacesClient placesClient;
    private String tag;
    private IPlaces iPlaces;

    public PlaceAutocompleteAdapter(Context context, String tag, ArrayList<JSONObject> mResultList)
    {
        if (!Places.isInitialized()) {
            String mapAPI = new UserSession(context).getAPI();
            if (mapAPI.equals(""))
            {
                mapAPI = context.getString(R.string.map_api_key);
            }
            Places.initialize(context, mapAPI);
        }

        this.mResultList = mResultList;
        mContext = context;
        this.tag = tag;
        placesClient = com.google.android.libraries.places.api.Places.createClient(context);
    }
    public void setClickListener(IPlaces iPlaces)
    {
        this.iPlaces = iPlaces;
    }

    @NonNull
    @Override
    public PlaceAutocompleteAdapter.PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result,parent,false);
        return new PlaceViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceAutocompleteAdapter.PlaceViewHolder holder, int position)
    {
        JSONObject item = mResultList.get(position);
        String address = item.optString("address");
        String area = item.optString("area");
        holder.address.setText(area);
        holder.area.setText(address);
//        holder.area.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView address;
        private CustomTextView area;

        PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            area = itemView.findViewById(R.id.area);

            itemView.setOnClickListener(v -> {
                if (!mResultList.isEmpty()) {
                    if (getAdapterPosition()>=0)
                    {
                        JSONObject item = mResultList.get(getAdapterPosition());

                        String placeId = item.optString("placeId");

                        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
                        placesClient.fetchPlace(request).addOnSuccessListener(response -> {
                            if (response != null) {
                                Place place = response.getPlace();
                                iPlaces.onClick(place, place.getName(), tag);
                            } else {
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.fail_error), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(exception -> {
                            if (exception instanceof ApiException) {
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.fail_error), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }*/

    private ArrayList<PlaceAutocomplete> mResultList;
    private Context mContext;
    private CharacterStyle styleBold;
    private CharacterStyle styleNormal;
    private final PlacesClient placesClient;
    private String tag;
    private IPlaces iPlaces;

    public PlaceAutocompleteAdapter(Context context, String tag) {
        String mapAPI = new UserSession(context).getAPI();
        if (mapAPI.equals("")) {
            mapAPI = context.getResources().getString(R.string.map_api_key);
        }
        Places.initialize(context, mapAPI);
        mResultList = new ArrayList<>();
        mContext = context;
        this.tag = tag;
//        this.animationView = animationView;
        styleBold = new StyleSpan(Typeface.BOLD);
        styleNormal = new StyleSpan(Typeface.NORMAL);
        placesClient = Places.createClient(context);
    }
    public void setClickListener(IPlaces iPlaces)
    {
        this.iPlaces = iPlaces;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint != null) {
                    mResultList = getPredictions(constraint);
                    if (!mResultList.isEmpty()) {
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
//                iPlaces.onResultFetched(tag);
                notifyDataSetChanged();
            }
        };
    }
    private ArrayList<PlaceAutocomplete> getPredictions(CharSequence constraint) {

        final ArrayList<PlaceAutocomplete> resultList = new ArrayList<>();

        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setCountry("IN")
                .setSessionToken(token)
                .setQuery(constraint.toString())
                .build();

        Task<FindAutocompletePredictionsResponse> autocompletePredictions = placesClient.findAutocompletePredictions(request);
        try {
            Tasks.await(autocompletePredictions, 60, TimeUnit.SECONDS);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            Thread.currentThread().interrupt();
        }

        if (autocompletePredictions.isSuccessful()) {
            FindAutocompletePredictionsResponse findAutocompletePredictionsResponse = autocompletePredictions.getResult();
            if (findAutocompletePredictionsResponse != null)
                for (AutocompletePrediction prediction : findAutocompletePredictionsResponse.getAutocompletePredictions()) {
                    resultList.add(new PlaceAutocomplete(prediction.getPlaceId(), prediction.getPrimaryText(styleNormal).toString(), prediction.getFullText(styleBold).toString()));
                }
            return resultList;
        } else {
            return resultList;
        }

    }
    @NonNull
    @Override
    public PlaceAutocompleteAdapter.PlaceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result,parent,false);
        return new PlaceViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlaceAutocompleteAdapter.PlaceViewHolder holder, int position) {
        holder.address.setText(mResultList.get(position).area);
        holder.area.setText(mResultList.get(position).address);
    }

    @Override
    public int getItemCount() {
        return mResultList.size();
    }

    class PlaceViewHolder extends RecyclerView.ViewHolder {
        private CustomTextView address;
        private CustomTextView area;

        PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.address);
            area = itemView.findViewById(R.id.area);

            itemView.setOnClickListener(v -> {
                if (!mResultList.isEmpty())
                {
                    if (getAdapterPosition() >= 0)
                    {
                        PlaceAutocomplete item = mResultList.get(getAdapterPosition());

                        String placeId = String.valueOf(item.placeId);

                        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
                        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();
                        placesClient.fetchPlace(request).addOnSuccessListener(response -> {
                            if (response != null)
                            {
                                Place place = response.getPlace();
                                iPlaces.onClick(place, item.area.toString(), tag);
                            }else
                            {
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.fail_error), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(exception -> {
                            if (exception instanceof ApiException) {
                                Toast.makeText(mContext, exception.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    public static class PlaceAutocomplete {

        private CharSequence placeId;
        private CharSequence address;
        private CharSequence area;

        PlaceAutocomplete(CharSequence placeId, CharSequence area, CharSequence address) {
            this.placeId = placeId;
            this.area = area;
            this.address = address;
        }

        @NotNull
        @Override
        public String toString() {
            return area.toString();
        }
    }
}
