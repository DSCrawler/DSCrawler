package crawling.discovery.html;

import java.net.URL;

import crawling.discovery.entities.Endpoint;

public class HttpEndpoint implements Endpoint {
	
	private URL url;

	public HttpEndpoint(URL url){
		this.setUrl(url);
	}

	public URL getUrl() {
		return url;
	}

	public void setUrl(URL url) {
		if(url == null){
			throw new IllegalArgumentException("Cannot set null URL for HttpEndpoint");
		}
		this.url = url;
	}
}
