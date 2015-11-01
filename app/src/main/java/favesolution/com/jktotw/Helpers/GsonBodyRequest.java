package favesolution.com.jktotw.Helpers;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by Daniel on 11/1/2015 for JktOtw project.
 */
public class GsonBodyRequest<T> extends JsonRequest<T> {
    private final Gson mGson = new Gson();
    private final Class<T> mClass;
    private final Response.Listener<T> mListener;
    private Map<String, String> mHeaders;
    private Priority mPriority;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     * @param method Method that request use
     */
    public GsonBodyRequest(int method,String url,String body,Class<T> clazz,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url,body,listener, errorListener);
        this.mClass = clazz;
        this.mListener = listener;
    }

    public void setPriority(Priority priority) {
        mPriority = priority;
    }
    public void setHeaders(Map<String,String> headers) {
        this.mHeaders = headers;
    }
    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders != null ? mHeaders : super.getHeaders();
    }
    @Override
    public Priority getPriority() {
        return mPriority == null ? Priority.NORMAL : mPriority;
    }
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(
                    mGson.fromJson(json, mClass),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }

    }
    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }
}
