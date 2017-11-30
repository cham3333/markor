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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: WIP
public class SttCommander {
    //
    // Statics
    //
    private static final String PT_DATE = "\\d{4}-\\d{2}-\\d{2}";
    public static final Pattern PATTERN_CONTEXTS = Pattern.compile("\\B(\\+\\w+)");
    public static final Pattern PATTERN_PROJECTS = Pattern.compile("\\B(\\@\\w+)"); // Project = Category
    public static final Pattern PATTERN_DONE = Pattern.compile("(?m)(^[Xx]) (.*)$");
    public static final Pattern PATTERN_DATE = Pattern.compile("(?:^|\\s|:)(" + PT_DATE + ")(?:$|\\s)");
    public static final Pattern PATTERN_KEY_VALUE_PAIRS = Pattern.compile("(?i)([a-z]+):([a-z0-9_-]+)");
    public static final Pattern PATTERN_PRIORITY_ANY = Pattern.compile("(?:^|\\n)\\(([A-Za-z])\\)\\s");
    public static final Pattern PATTERN_PRIORITY_A = Pattern.compile("(?:^|\\n)\\(([Aa])\\)\\s");
    public static final Pattern PATTERN_PRIORITY_B = Pattern.compile("(?:^|\\n)\\(([Bb])\\)\\s");
    public static final Pattern PATTERN_PRIORITY_C = Pattern.compile("(?:^|\\n)\\(([Cc])\\)\\s");
    public static final Pattern PATTERN_PRIORITY_D = Pattern.compile("(?:^|\\n)\\(([Dd])\\)\\s");
    public static final Pattern PATTERN_PRIORITY_E = Pattern.compile("(?:^|\\n)\\(([Ee])\\)\\s");
    public static final Pattern PATTERN_PRIORITY_F = Pattern.compile("(?:^|\\n)\\(([Ff])\\)\\s");
    public static final Pattern PATTERN_COMPLETION_DATE = Pattern.compile("(?:^|\\n)(?:[Xx] )(" + PT_DATE + ")");
    public static final Pattern PATTERN_CREATION_DATE = Pattern.compile("(?:^|\\n)(?:[Xx] " + PT_DATE + " )?(" + PT_DATE + ")");

    //
    // Singleton
    //
    private static SttCommander __instance;

    public static SttCommander get() {
        if (__instance == null) {
            __instance = new SttCommander();
        }
        return __instance;
    }

    //
    // Members, Constructors
    //
    public int lastParseTextStartOffset;

    public SttCommander() {

    }

    //
    // Parsing Methods
    //

    public SttTask parseTask(String text, int cursorPosInDocument) {
        String line = "";
        if (text != null && cursorPosInDocument < text.length()) {
            int lineStart = text.lastIndexOf('\n', cursorPosInDocument);
            lineStart = lineStart == -1 ? 0 : lineStart + 1;
            if (lineStart < text.length()) {
                int lineEnd = text.indexOf('\n', lineStart);
                lineEnd = lineEnd == -1 ? text.length() : lineEnd;
                line = text.substring(lineStart, lineEnd);
            }
            lastParseTextStartOffset = cursorPosInDocument - lineStart;
        }
        return parseTask(line);
    }

    public SttTask parseTask(final String line) {
        SttTask task = new SttTask();
        task.setText(line);
        task.setProjects(parseProjects(line));
        task.setContexts(parseContexts(line));
        task.setDone(parseDone(line));
        task.setCompletionDate(parseCompletionDate(line));
        task.setCreationDate(parseCreationDate(line));
        task.setPriority(parsePriority(line));
        task.setKeyValuePairs(parseKeyValuePairs(line));

        System.out.print("");
        return task;
    }

    public List<String> parseContexts(String text) {
        return parseAllUniqueMatches(text, PATTERN_CONTEXTS);
    }


    public List<String> parseProjects(String text) {
        return parseAllUniqueMatches(text, PATTERN_PROJECTS);
    }

    private boolean parseDone(String line) {
        return isPatternFindable(line, PATTERN_DONE);
    }

    private String parseCompletionDate(String line) {
        return parseOneValueOrDefault(line, PATTERN_COMPLETION_DATE, "");
    }

    private String parseCreationDate(String line) {
        return parseOneValueOrDefault(line, PATTERN_CREATION_DATE, "");
    }

    private char parsePriority(String line) {
        String ret = parseOneValueOrDefault(line, PATTERN_PRIORITY_ANY, "");
        if (ret.length() == 1) {
            return ret.charAt(0);
        } else {
            return SttTask.PRIORITY_NONE;
        }
    }

    private Map<String, String> parseKeyValuePairs(String line) {
        Map<String, String> values = new HashMap<>();
        for (String kvp : parseAllUniqueMatches(line, PATTERN_KEY_VALUE_PAIRS)) {
            int s = kvp.indexOf(':');
            values.put(kvp.substring(0, s), kvp.substring(s + 1));
        }
        return values;
    }


    //
    // Applying methods
    //
    public void insertProject(SttTask task, String project, int atIndex) {
        String text = task.getText();
        project = project.startsWith("@") ? project.substring(1) : project;
        String[] split = splitAtIndexFailsafe(text, atIndex);

        String left = split[0];
        String right = split[1];
        left = (!left.endsWith(" ") && !left.isEmpty()) ? (left + " ") : left;
        right = (!right.startsWith(" ") && !right.isEmpty()) ? (" " + right) : right;
        task.setText(left + "@" + project + right);
        List<String> projects = task.getProjects();
        if (!projects.contains(project)) {
            projects.add(project);
        }
    }

    public void insertContext(SttTask task, String context, int atIndex) {
        String text = task.getText();
        context = context.startsWith("+") ? context.substring(1) : context;
        String[] split = splitAtIndexFailsafe(text, atIndex);

        String left = split[0];
        String right = split[1];
        left = (!left.endsWith(" ") && !left.isEmpty()) ? (left + " ") : left;
        right = (!right.startsWith(" ") && !right.isEmpty()) ? (" " + right) : right;
        task.setText(left + "+" + context + right);
        List<String> contexts = task.getContexts();
        if (!contexts.contains(context)) {
            contexts.add(context);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    private String[] splitAtIndexFailsafe(String text, int atIndex) {
        String left = "";
        String right = "";

        if (text == null || text.isEmpty()) {

        } else if (atIndex >= text.length()) {
            left = text;
        } else if (atIndex < 0) {
            right = text;
        } else {
            left = text.substring(0, atIndex);
            right = text.substring(atIndex);
        }


        return new String[]{left, right};
    }

    //
    //
    //
    private static List<String> parseAllUniqueMatches(String text, Pattern pattern) {
        List<String> ret = new ArrayList<>();
        for (Matcher m = pattern.matcher(text); m.find(); ) {
            String found = m.group();
            if (!ret.contains(found)) {
                ret.add(found);
            }
        }
        return ret;
    }

    private static String parseOneValueOrDefault(String text, Pattern pattern, String defaultValue) {
        for (Matcher m = pattern.matcher(text); m.find(); ) {
            // group / group(0) => everything, including non-capturing. group 1 = first capturing group
            if (m.groupCount() > 0) {
                return m.group(1);
            }
        }
        return defaultValue;
    }

    private static boolean isPatternFindable(String text, Pattern pattern) {
        return pattern.matcher(text).find();
    }
}
