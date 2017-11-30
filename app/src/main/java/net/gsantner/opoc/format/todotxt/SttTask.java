/*
 * ------------------------------------------------------------------------------
 * Gregor Santner <gsantner.net> wrote this. You can do whatever you want
 * with it. If we meet some day, and you think it is worth it, you can buy me a
 * coke in return. Provided as is without any kind of warranty. Do not blame or
 * sue me if something goes wrong. No attribution required.    - Gregor Santner
 *
 * License: Creative Commons Zero (CC0 1.0)
 *  http://creativecommons.org/publicdomain/zero/1.0/
 * ----------------------------------------------------------------------------
 */
package net.gsantner.opoc.format.todotxt;

import net.gsantner.opoc.data.MapPropertyBackend;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SttTask implements Serializable {
    //
    // Statics
    //
    private static final int PROP_ID = 1;
    private static final int PROP_TEXT = 2;
    private static final int PROP_KEY_VALUE_PAIRS = 3;
    private static final int PROP_DONE = 11;
    private static final int PROP_PRIORITY = 12;
    private static final int PROP_CREATION_DATE = 13;
    private static final int PROP_COMPLETION_DATE = 14;
    private static final int PROP_CONTEXTS = 15;
    private static final int PROP_PROJECTS = 16;

    public static final char PRIORITY_NONE = (char) -1;

    //
    // Members & Constructor
    //
    private MapPropertyBackend<Integer> _data = new MapPropertyBackend<>();
    private Map<String, String> _dataKeyValuePair = new HashMap<>();


    public SttTask() {
    }

    //
    // Methods
    //

    public long getId() {
        return _data.getLong(PROP_ID, -1);
    }

    public SttTask setId(long value) {
        _data.setLong(PROP_ID, value);
        return this;
    }

    public boolean isDone() {
        return _data.getBool(PROP_DONE, false);
    }

    public SttTask setDone(boolean value) {
        _data.setBool(PROP_DONE, value);
        return this;
    }

    public Map<String, String> getKeyValuePairs() {
        return _dataKeyValuePair;
    }

    public SttTask setKeyValuePairs(Map<String, String> values) {
        _dataKeyValuePair = values;
        return this;
    }

    public String getKeyValuePair(String key, String defaultValue) {
        return _dataKeyValuePair.containsKey(key) ? _dataKeyValuePair.get(key) : defaultValue;
    }

    public SttTask setKeyValuePair(String key, String value) {
        _dataKeyValuePair.put(key, value);
        return this;
    }

    public char getPriority() {
        return (char) _data.getInt(PROP_PRIORITY, PRIORITY_NONE);
    }

    public SttTask setPriority(char value) {
        _data.setInt(PROP_PRIORITY, value);
        return this;
    }

    public String getText() {
        return _data.getString(PROP_TEXT, "");
    }

    public SttTask setText(String value) {
        _data.setString(PROP_TEXT, value);
        return this;
    }

    public List<String> getContexts() {
        return _data.getStringList(PROP_CONTEXTS);
    }

    public SttTask setContexts(List<String> value) {
        _data.setStringList(PROP_CONTEXTS, value);
        return this;
    }

    public List<String> getProjects() {
        return _data.getStringList(PROP_PROJECTS);
    }

    public SttTask setProjects(List<String> value) {
        _data.setStringList(PROP_PROJECTS, value);
        return this;
    }

    public String getCreationdueDate() {
        return _data.getString(PROP_CREATION_DATE, "");
    }

    public SttTask setCreationDate(String value) {
        _data.setString(PROP_CREATION_DATE, value);
        return this;
    }

    public String getCompletionDate() {
        return _data.getString(PROP_COMPLETION_DATE, "");
    }

    public SttTask setCompletionDate(String value) {
        _data.setString(PROP_COMPLETION_DATE, value);
        return this;
    }


}
