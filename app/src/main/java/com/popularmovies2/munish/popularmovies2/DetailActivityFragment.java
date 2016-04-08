package com.popularmovies2.munish.popularmovies2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {
    private final   String LOG_TAG =DetailActivityFragment.class.getSimpleName();
   // private MovieDto mDto;
    public DetailActivityFragment() {
        //mDto=new MovieDto();
    }

//private MyAdapter mAdapter;
private String BASE_URL = "http://image.tmdb.org/t/p/w500//";
    private String BASE_VIDEO_REVIEWS_URL = "http://api.themoviedb.org/3/movie/";

    private ArrayAdapter<Trailer> mTrailerAdapter;
    private ArrayAdapter<String> mReviewsAdapter;

    public class TrailerAdapter extends ArrayAdapter<Trailer>
    {

        public TrailerAdapter(Context context, int resource, int textViewResourceId, List<Trailer> objects) {
            super(context, resource, textViewResourceId, objects);
        }
    }

public class Trailer
{
    @Override
    public String toString() {
        return trailerTitle;
    }


    public Trailer(String key, String title)
    {
        this.trailerKey=key;
        this.trailerTitle=title;
    }
    private String trailerKey;

    public String getTrailerKey() {
        return trailerKey;
    }

    public void setTrailerKey(String trailerKey) {
        this.trailerKey = trailerKey;
    }

    private String trailerTitle;
}


    private ImageView mImage;
    private TextView mTitle;
    private TextView mYear;
    private String mPath;

    private TextView mRuntime;
    private TextView mVote;
    private TextView mOverView;
    private ListView mListView;
    private ListView mReviewListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        // The detail Activity called via intent.  Inspect the intent for forecast data.
        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra(Intent.EXTRA_TEXT)) {
            String movieId = intent.getStringExtra(Intent.EXTRA_TEXT);
            FetchMovieDetails task = new FetchMovieDetails();

            Log.v(LOG_TAG, "inside detailActivit");

            task.execute(movieId);
            mTitle=(TextView) rootView.findViewById(R.id.title);
            mImage=(ImageView) rootView.findViewById(R.id.poster);
          //  mPath=BASE_URL+mPath;
           // Picasso.with(getContext()).load(mPath).into(mImage);
            mYear=(TextView) rootView.findViewById(R.id.year);
            mRuntime=(TextView)rootView.findViewById(R.id.runTime);
            mVote= (TextView)rootView.findViewById(R.id.vote);
            mOverView= (TextView)rootView.findViewById(R.id.overView);

            Log.v(LOG_TAG,"title_oncreate: "+mTitle.getText());


            mTrailerAdapter =
                    new ArrayAdapter<Trailer>(
                            getActivity(), // The current context (this activity)
                            R.layout.list_item_trailer, // The name of the layout ID.
                            R.id.list_item_trailer_textview, // The ID of the textview to populate.
                            new ArrayList<Trailer>());
            mListView = (ListView) rootView.findViewById(R.id.listView);
            mListView.setAdapter(mTrailerAdapter);


            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    String videoKey = mTrailerAdapter.getItem(position).getTrailerKey();

                    // String videoId = "Fee5vbFLYM4";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoKey));
                    intent.putExtra("VIDEO_ID", videoKey);
                    startActivity(intent);
                }
            });

        mReviewsAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.reviews_list_item_trailer,
                R.id.reviews_list_item_trailer_textview,
                new ArrayList<String>());

            mReviewListView = (ListView) rootView.findViewById(R.id.reviewListView);
            mReviewListView.setAdapter(mReviewsAdapter);



        }

        return rootView;
    }


    public class MovieDto
    {
        private String title;
        private String relDate;
        private String voteAvg;
        private String runtime;
        private String posterUrl;
        private String overView;
        private ArrayList<String> trailerKeys;
        private ArrayList<String> reviews;

        public ArrayList<String> getReviews() {
            return reviews;
        }

        public void setReviews(ArrayList<String> reviews) {
            this.reviews = reviews;
        }

        public ArrayList<String> getTrailerKeys() {
            return trailerKeys;
        }

        public void setTrailerKeys(ArrayList<String> trailerKeys) {
            this.trailerKeys = trailerKeys;
        }

        public String getOverView() {
            return overView;
        }

        public void setOverView(String overView) {
            this.overView = overView;
        }

        public String getPosterUrl() {
            return posterUrl;
        }

        public void setPosterUrl(String posterUrl) {
            this.posterUrl = posterUrl;
        }



        public String getTitle() {
            return title;
        }

        public String getRuntime() {
            return runtime;
        }

        public void setRuntime(String runtime) {
            this.runtime = runtime;
        }

        public String getVoteAvg() {
            return voteAvg;
        }

        public void setVoteAvg(String voteAvg) {
            this.voteAvg = voteAvg;
        }

        public String getRelDate() {
            return relDate;
        }

        public void setRelDate(String relDate) {
            this.relDate = relDate;
        }

        public void setTitle(String title) {
            this.title = title;

        }


    }

    public class FetchMovieDetails extends AsyncTask<String, Void, MovieDto>
    {
        String BASE_MOVIE_DETAIL_URl = "http://api.themoviedb.org/3/movie/";


        private final   String LOG_TAG =FetchMovieDetails.class.getSimpleName();
        @Override
        protected MovieDto doInBackground(String... params) {
            // These two need to be declared outside the try/catch
// so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

// Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            try {
                Log.v(LOG_TAG, "params : " + params[0]);
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                URL[] urls = new URL[3];



                URL url = new URL(BASE_MOVIE_DETAIL_URl + params[0] + "?api_key="+BuildConfig.OPEN_MOVIES_API_KEY);
                URL urlVedio = new URL(BASE_VIDEO_REVIEWS_URL + params[0] + "/videos?api_key="+BuildConfig.OPEN_MOVIES_API_KEY);
                URL urlReviews = new URL(BASE_VIDEO_REVIEWS_URL + params[0] + "/reviews?api_key="+BuildConfig.OPEN_MOVIES_API_KEY);

                urls[0]=url;
                urls[1]= urlVedio;
                urls[2]=urlReviews;


                Log.v(LOG_TAG,"Entry 1");
                JSONObject[] jsonObjects = getJsonObjects(urls);
                return  getMovieDetails(jsonObjects);

            }
             catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        private JSONObject[] getJsonObjects(URL[] urls){
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            Log.v(LOG_TAG,"Entry 2");
            JSONObject[] jsonObjs=new JSONObject[urls.length];
            for(int i = 0; i<urls.length;i++)
            {
                URL url = urls[i];
                Log.v(LOG_TAG,"..url.."+url);
                String jsonStr="";
                try {
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.connect();

                    // Read the input stream into a String
                    InputStream inputStream = urlConnection.getInputStream();
                    StringBuffer buffer = new StringBuffer();
                    if (inputStream == null) {
                        // Nothing to do.
                        jsonStr = null;
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
                        jsonStr = null;
                    }
                    jsonStr = buffer.toString();

                    JSONObject movieObj = new JSONObject(jsonStr);
                    jsonObjs[i] = movieObj;
                }
                catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                } catch (JSONException e) {
                    e.printStackTrace();
                    jsonStr = "";
                }
                finally {
                    if (urlConnection != null) {
                        urlConnection.disconnect();
                    }
                    if (reader != null) {
                        try {
                            reader.close();


                        } catch (final IOException e) {
                            Log.e("PlaceholderFragment", "Error closing stream", e);
                        }
                    }
                }

            }
            return jsonObjs;
        }
        private MovieDto getMovieDetails(JSONObject[] jsonObjects) throws JSONException {
            MovieDto movieDto= new MovieDto();

            Log.v(LOG_TAG,"Entry 3");
            JSONObject movieObj = jsonObjects[0];
            //JSONObject movieObj = new JSONObject(jsonStr);
            movieDto.setTitle(movieObj.getString("title"));
            movieDto.setRuntime(movieObj.getString("runtime"));
            movieDto.setVoteAvg(movieObj.getString("vote_average"));
            movieDto.setRelDate(movieObj.getString("release_date"));
            movieDto.setPosterUrl(movieObj.getString("poster_path"));
            movieDto.setOverView(movieObj.getString("overview"));

            JSONObject trailerObj = jsonObjects[1];
            JSONArray arr = trailerObj.getJSONArray("results");
            ArrayList<String> temp = new ArrayList<String>();
            for(int i=0 ; i< arr.length();i++)
            {

                JSONObject obj = arr.getJSONObject(i);
                temp.add(obj.getString("key"));
                Log.v(LOG_TAG, "trailer ids: " +obj.getString("key"));
            }
            movieDto.setTrailerKeys(temp);
            Log.v(LOG_TAG, "movieDto iii2: " + movieDto);

            JSONObject reviewsObj = jsonObjects[2];
            JSONArray array = reviewsObj.getJSONArray("results");
            ArrayList<String> t=new ArrayList<String>();
            for(int i = 0 ; i< array.length();i++)
            {
                JSONObject obj = array.getJSONObject(i);
                t.add(obj.getString("content"));
            }
            movieDto.setReviews(t);

            //mTitle.setText(movieObj.getString("title"));
            Log.v(LOG_TAG, "movieDto iii: " + movieDto);
            return movieDto;
        }

        @Override
        protected void onPostExecute(MovieDto movieDto) {
            //mAdapter.dto = movieDto;
            Log.v(LOG_TAG, "movieDto: " + movieDto);

            mTitle.setText(movieDto.getTitle());
            mPath=movieDto.getPosterUrl();
            String year = movieDto.getRelDate().split("-")[0];
            mYear.setText(year);
            mRuntime.setText(movieDto.getRuntime() + "min");
            mVote.setText(movieDto.getVoteAvg() + "/10");
            mOverView.setText(movieDto.getOverView());


            mPath=BASE_URL+mPath;
            Picasso.with(getContext()).load(mPath).into(mImage);
            int i =0;
            for(String s : movieDto.getTrailerKeys()) {
                i++;
                Trailer tr=new Trailer(s,"Trailer"+i);
                mTrailerAdapter.add(tr);

            }
           for(String ss:movieDto.getReviews())
           {
               mReviewsAdapter.add(ss);
           }
            Log.v(LOG_TAG, "title_oncreate: " + mTitle.getText());
            Log.v(LOG_TAG, "Year: " + mYear.getText());
            Log.v(LOG_TAG, "path: " + mPath);



          /*  Log.v(LOG_TAG,"posttitle " +movieDto.getTitle());
            mDto=movieDto;*/
        }
    }
}
