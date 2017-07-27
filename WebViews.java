package weatherapp.barant2003.com.weatherapplications;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebViewClient;

/**
 * Created by baran_000 on 7/21/2017.
 */

   public class WebViews extends Fragment {



    android.webkit.WebView webView;
    Context mContext;
    String myUrl;

    String url = "http://api.wunderground.com/api/56815a4f15520bbc/animatedradar/q/FL/Orlando.png?num=6&delay=50&interval=30";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.map_fragment, container, false);
        webView = (android.webkit.WebView) view.findViewById(R.id.webView);

        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new MyWebViewClient());

        // GifMovieView(mContext, myUrl);

        if (myUrl == null) {
            myUrl = url;
        }
        webView.loadUrl(myUrl);

        return view;

    }


    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
            myUrl = url;
            view.loadUrl(url);
            return true;
        }




    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }
}
