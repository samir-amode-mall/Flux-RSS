package com.example.fluxrss;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class MyRSSsaxHandler extends DefaultHandler {
    private String _url = null;
    private boolean _inTitle = false;
    private boolean _inDescription = false;
    private boolean _inItem = false;
    private boolean _inDate = false;

    private Item _currentItem = null;
    private String _imageURL = null;

    private int _numItem = 0;
    private int _numItemMax = -1;

    private List<Item> _itemList = new ArrayList<>();

    public void setUrl(String url) {
        _url = url;
    }

    public void processFeed() {
        try {
            _itemList.clear();
            _numItem = 0;
            _numItemMax = -1;

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser parser = factory.newSAXParser();
            XMLReader reader = parser.getXMLReader();
            reader.setContentHandler(this);
            InputStream inputStream = new URL(_url).openStream();
            reader.parse(new InputSource(inputStream));

            _numItemMax = _itemList.size();
        } catch (Exception e) {
            Log.e("rssview", "Erreur lors du traitement du flux RSS: " + e.getMessage());
        }
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase("item")) {
            _inItem = true;
            _currentItem = new Item();
        } else if (_inItem) {
            if (qName.equalsIgnoreCase("title")) {
                _inTitle = true;
            } else if (qName.equalsIgnoreCase("description")) {
                _inDescription = true;
            } else if (qName.equalsIgnoreCase("pubDate")) {
                _inDate = true;
            } else if (qName.equalsIgnoreCase("media:content")) {
                _imageURL = attributes.getValue("url");
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String chars = new String(ch, start, length);
        if (_inTitle) {
            _currentItem.getTitle().append(chars);
        } else if (_inDescription) {
            _currentItem.getDescription().append(chars);
        } else if (_inDate) {
            _currentItem.getDate().append(chars);
        }
    }

    private Bitmap getBitmap(String imageUrl) {
        try (InputStream inputStream = new URL(imageUrl).openStream()) {
            return BitmapFactory.decodeStream(inputStream);
        } catch (Exception e) {
            Log.e("MyRSSsaxHandler", "Erreur lors du téléchargement de l'image: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase("item")) {
            if (_imageURL != null) {
                _currentItem.setImage(getBitmap(_imageURL));
            }
            _itemList.add(_currentItem);
            _inItem = false;
        } else if (qName.equalsIgnoreCase("title")) {
            _inTitle = false;
        } else if (qName.equalsIgnoreCase("description")) {
            _inDescription = false;
        } else if (qName.equalsIgnoreCase("pubDate")) {
            _inDate = false;
        }
    }

    public Item getFirstItem() {
        return !_itemList.isEmpty() ? _itemList.get(0) : null;
    }

    public Item nextItem() {
        if (_numItem < _numItemMax - 1) {
            _numItem++;
        }
        return _itemList.get(_numItem);
    }

    public Item prevItem() {
        if (_numItem > 0) {
            _numItem--;
        }
        return _itemList.get(_numItem);
    }
}
