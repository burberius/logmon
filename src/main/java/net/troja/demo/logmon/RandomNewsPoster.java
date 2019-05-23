package net.troja.demo.logmon;

import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.CloseableThreadContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class RandomNewsPoster {
    private static final Logger LOGGER = LogManager.getLogger(RandomNewsPoster.class);

    private final List<String> news = new ArrayList<>();
    private final Random rand = new Random(System.currentTimeMillis());
    private final Calendar calendar = Calendar.getInstance();

    public RandomNewsPoster() {
        LOGGER.info("Starting random news poster");
    }

    @Scheduled(fixedRate = 6000)
    public void post() {
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (!news.isEmpty() && (rand.nextInt(100) < (2 + (2 * calendar.get(Calendar.HOUR))))) {
            final String entry = news.get(rand.nextInt(news.size()));
            final String name = entry.replaceAll(".*\\((.*)\\)", "$1");
            try (final CloseableThreadContext.Instance ctc = CloseableThreadContext.put("uuid", name)) {
                LOGGER.info(news.get(rand.nextInt(news.size())));
            }
        }
    }

    @Scheduled(fixedRate = 600000)
    public void collect() {
        news.clear();
        getFeed("BBC World", "http://feeds.bbci.co.uk/news/world/rss.xml");
        getFeed("Reuters World", "http://feeds.reuters.com/Reuters/worldNews");
        getFeed("Tagesschau", "http://www.tagesschau.de/xml/rss2");
        LOGGER.info("Collected " + news.size() + " news entries all together");
        Collections.shuffle(news);
    }

    public void getFeed(String name, String url) {
        try (CloseableHttpClient client = HttpClients.createMinimal()) {
            final HttpUriRequest method = new HttpGet(url);
            try (CloseableHttpResponse response = client.execute(method); InputStream stream = response.getEntity().getContent()) {
                final SyndFeedInput input = new SyndFeedInput();
                final SyndFeed feed = input.build(new XmlReader(stream));
                for (final SyndEntry entry : feed.getEntries()) {
                    news.add(entry.getPublishedDate() + " - " + entry.getTitle() + " (" + name + ")");
                }
                LOGGER.info("Got " + feed.getEntries().size() + " entries from " + name);
            } catch (IllegalArgumentException | FeedException e) {
                LOGGER.error("Could not parse news data", e);
            }
        } catch (final IOException e) {
            LOGGER.error("Could not get news data", e);
        }

    }
}