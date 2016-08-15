package com.popularmovies2.munish.popularmovies2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment  implements AdapterView.OnItemClickListener {
    private String BASE_URL = "http://image.tmdb.org/t/p/w600//";
    private final   String LOG_TAG = MainActivityFragment.class.getSimpleName();
    private ImageAdapter mAdapter;
    private String lastSetting="";
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


    }

    public MainActivityFragment() {
    }

    public interface Callback {
        /**
         * DetailFragmentCallback for when an item has been selected.
         */
        public void  onItemSelected(String selectedMovieId);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "Inside OnStart");
        mAdapter.notifyDataSetChanged();
        if( mAdapter.mMovieIds.isEmpty()) {
            FetchPopularMoviesTask task = new FetchPopularMoviesTask();
            task.execute();
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if(!lastSetting.equalsIgnoreCase(Utility.getPreferedRequest(getContext()))) {
            mAdapter.mMovieIds.clear();
            FetchPopularMoviesTask task = new FetchPopularMoviesTask();
            task.execute();
        }
      //  super.onResume();
        this.onCreate(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        mAdapter = new ImageAdapter(getActivity());
        Log.v(LOG_TAG,"oncreateview "+mAdapter.mMovieIds);

        //while(mAdapter.mMovieIds.size())
        gridview.setAdapter( mAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Long selectedMovieId =  mAdapter.getItemId(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, selectedMovieId.toString());
                startActivity(intent);
            }


        });
        return rootView;
        //return inflater.inflate(R.layout.fragment_main, container, false);
    }

    public class ImageAdapter extends BaseAdapter {
        private Context mContext;


        public ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mMovieIds.size();
            //return 2;
        }

        public Object getItem(int position) {
            Log.v(LOG_TAG, "Position : " + position);
            String url="";
            Map<String, String> map =mMovieIds.get(position);
            Enumeration e = Collections.enumeration(mMovieIds.get(position).values());
            while(e.hasMoreElements()) {
                url = e.nextElement().toString();
            }
            Log.v(LOG_TAG,"url : " +url);
            return url;
            // return "http://image.tmdb.org/t/p/w780///inVq3FRqcYIRl2la8iZikYYxFNR.jpg?api_key=a0083c29a4115493069418153f6939ed";
        }

        public long getItemId(int position) {
            Log.v(LOG_TAG, "Position : " + position);
            String id="";
            Map<String, String> map =mMovieIds.get(position);
            Enumeration e = Collections.enumeration(mMovieIds.get(position).keySet());
            while(e.hasMoreElements()) {
                id = e.nextElement().toString();
            }
            Log.v(LOG_TAG,"id : " +id);
            return Long.parseLong(id);
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER);


                //imageView.setLayoutParams();
                // int h = mContext.getResources().getDisplayMetrics().densityDpi;
                //  convertView.setLayoutParams(new GridView.LayoutParams(h-45, h-39));
                // imageView.setPadding(8, 8, 8, 8);
            } else {
                imageView = (ImageView) convertView;
            }

            String url = (String)getItem(position);
            Log.v(LOG_TAG,"url : " +url);
            //url = "http://image.tmdb.org/t/p/w500///nN4cEJMHJHbJBsp3vvvhtNWLGqg.jpg";
              //    "http://image.tmdb.org/t/p/w500///inVq3FRqcYIRl2la8iZikYYxFNR.jpg"
            // Picasso.with(context).load(url).into(view);
            Picasso.with(mContext).load(url).into(imageView);
            //imageView.setImageResource(mThumbIds[position]);
            return imageView;
        }


        // references to our images
        private List<Map<String, String>> mMovieIds =new ArrayList<Map<String, String>>();

      /*  {
                "http://image.tmdb.org/t/p/w780///inVq3FRqcYIRl2la8iZikYYxFNR.jpg?api_key=a0083c29a4115493069418153f6939ed"
                *//* R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7,
                R.drawable.sample_0, R.drawable.sample_1,
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7,
                R.drawable.sample_0, R.drawable.sample_1,
                R.drawable.sample_2, R.drawable.sample_3,
                R.drawable.sample_4, R.drawable.sample_5,
                R.drawable.sample_6, R.drawable.sample_7*//*
        };*/
    }

    public class FetchPopularMoviesTask extends AsyncTask<Void, Void, List<Map<String, String>>> {

        private final   String LOG_TAG = FetchPopularMoviesTask.class.getSimpleName();

        @Override
        protected List<Map<String, String>> doInBackground (Void... params){
            // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.

            Log.v(LOG_TAG,"Inside doInBackground");
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;


// Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            Map<String,String>    movieData=new HashMap<String,String>();
            List<Map<String,String>> movieList = new ArrayList<Map<String,String> >();

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL url =null;
              String request = Utility.getPreferedRequest(getContext());
                lastSetting=request;
                Log.v(LOG_TAG,"request====="+request);
                if(request.equalsIgnoreCase(getContext().getString(R.string.pref_request_popular))) {

                     url = new URL("http://api.themoviedb.org/3/movie/popular?api_key=" + BuildConfig.OPEN_MOVIES_API_KEY);
                }
              //  else if(request.equalsIgnoreCase(getContext().getString(R.string.pref_request_label_top)))
                else if(request.equalsIgnoreCase(getContext().getString(R.string.pref_request_top)))
                     url = new URL("http://api.themoviedb.org/3/movie/top_rated?api_key=" + BuildConfig.OPEN_MOVIES_API_KEY);


                if(url!=null) {
                    // Create the request to OpenWeatherMap, and open the connection
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        forecastJsonStr = null;
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));

                    String line;
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }

                    if (buffer.length() == 0) {
                        // Stream was empty.  No point in parsing.
                        forecastJsonStr = null;
                    }
                    forecastJsonStr = buffer.toString();

                    Log.v(LOG_TAG, "JSON STRING: " + forecastJsonStr);
                }
                 if(request.equalsIgnoreCase(getContext().getString(R.string.pref_request_fav)))
                {
                    Log.v(LOG_TAG, "favourite movies condition");
                    SharedPreferences prefs = getContext().getSharedPreferences("Fav_Movies", Context.MODE_PRIVATE);
                    HashMap<String,String> favMap = (HashMap<String,String>) prefs.getAll();
                    for(String s : favMap.keySet())
                    {
                        movieData.put(s,favMap.get(s));
                        movieList.add(movieData);
                        movieData = new HashMap<String,String>();
                    }

                    /*String movieListStr = prefs.getString("MovieList", "131635;http://image.tmdb.org/t/p/w500///wM0BxA2zOtHW3f0xWZC9FcMLWl5.jpg");
                    String[]  arr = movieListStr.split(",");
                    if(arr.length>0) {
                        for (String ss : arr) {
                            String[] arrInner = ss.split(";");
                            movieData.put(arrInner[0], BASE_URL + arrInner[1]);
                            movieList.add(movieData);
                            movieData = new HashMap<String, String>();
                        }
                    }*/
                    // url = new URL("http://api.themoviedb.org/3/movie/top_rated?api_key=" + BuildConfig.OPEN_MOVIES_API_KEY);
                }
            } catch (IOException e) {
                Log.e("PlaceholderFragment", "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                        if(movieList.size()==0) {
                            movieList = getMovieData(forecastJsonStr);
                        }

                    } catch (final IOException e) {
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return  movieList;
        }

        private List<Map<String, String>> getMovieData(String jsonStr) throws JSONException {
            final String OWM_LIST = "results";
            Map<String,String> movieData;
            List<Map<String,String>> movieList = new ArrayList<Map<String,String> >();
            JSONObject movieJson = new JSONObject(jsonStr);
            JSONArray movieArray = movieJson.getJSONArray(OWM_LIST);

            String posterPath = "";
            String movieId="";
            for(int i = 0;i< movieArray.length(); i++)
            {
                movieData=new HashMap<String,String>();
                JSONObject masterData = movieArray.getJSONObject(i);
                posterPath=BASE_URL+masterData.getString("poster_path");
                movieId = masterData.getString("id");
               // Log.v(LOG_TAG,"poster_path : for location :"+i+"  "+posterPath);
                movieData.put(movieId, posterPath);
                movieList.add(movieData);
               // Log.v(LOG_TAG, "id : " + movieId + "URl : " + BASE_URL + posterPath);
            }
            return movieList;
            // return  (String[])paths.toArray(new String[paths.size()]);

        }
        @Override
        protected void onPostExecute(List<Map<String, String>> result) {

            if(result != null){

                 for(int i =0; i< result.size(); i++) {
                   //  for(int i =0; i<2; i++) {
                    mAdapter.mMovieIds.add(result.get(i));
                    Log.v(LOG_TAG,BASE_URL+result.get(i));
                }
                mAdapter.notifyDataSetChanged();
            }

            // mAdapter.mUriIds.add("http://image.tmdb.org/t/p/w342///inVq3FRqcYIRl2la8iZikYYxFNR.jpg");
            // mAdapter.mUriIds.add("http://image.tmdb.org/t/p/w342///nN4cEJMHJHbJBsp3vvvhtNWLGqg.jpg");
            // mAdapter.mUriIds.add("http://image.tmdb.org/t/p/w342///sM33SANp9z6rXW8Itn7NnG1GOEs.jpg");
            //  mAdapter.mUriIds.add("http://image.tmdb.org/t/p/w342///kqjL17yufvn9OVLyXYpvtyrFfak.jpg");
            //  mAdapter.mUriIds.add("http://image.tmdb.org/t/p/w342///6bCplVkhowCjTHXWv49UjRPn0eK.jpg");
            //  mAdapter.mUriIds.add("http://image.tmdb.org/t/p/w342///cWERd8rgbw7bCMZlwP207HUXxym.jpg");


        }
    }


}
