package com.noboru.webscraping.util;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.noboru.webscraping.util.exception.WebScrapingException;

public final class WebScrapingUtil {

    private static final int DELAY = 2;

    public static Boolean isURLValid(String url) {
        return url.startsWith("http://") || url.startsWith("https://");
    }

    public static Boolean isURLInvalid(String url) {
        return !WebScrapingUtil.isURLValid(url);
    }

    public static String getHTML(String url) throws WebScrapingException {
        String html;
        URLConnection urlConnection;
        try {
            TimeUnit.SECONDS.sleep(DELAY);
            urlConnection =  new URL(url).openConnection();
            Scanner scanner = new Scanner(urlConnection.getInputStream());
            scanner.useDelimiter("\\Z");
            html = scanner.next();
        }catch (Exception e) {
            throw new WebScrapingException("Problem trying to get html from url: " + url);
        }
        return html.replaceAll("\n", "");
    }

    public static List<String> getListGroupContentFromPattern(String strPattern, Integer numGroup, String html) {
        Pattern pattern = Pattern.compile(strPattern);
        Matcher matcher = pattern.matcher(html);
        List<String> contents = new ArrayList<>();
        while(matcher.find()) {
            String content = matcher.group(numGroup);
            contents.add(content);
        }
        return contents;
    }

    public static String getContentFromPattern(String strPattern, Integer numGroup, String html) {
        Pattern pattern = Pattern.compile(strPattern);
        Matcher matcher = pattern.matcher(html);
        matcher.find();
        String content = matcher.group(numGroup);
        return content;
    }
}
