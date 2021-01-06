package com.che.hadoop.logclean.utils;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author 张松伟
 * Json解析类
 */
public class JsonQuery implements Iterable<String>{
    private Map<String, String> m = new HashMap<String, String>();
    private String s;
    private List<String> l = new ArrayList<String>();

    /**
     *
     * @param s 需要解析的字符串
     */
    public JsonQuery(String s) {
        this(s, false);
    }

    /**
     *
     * @param s 需要解析的字符串
     * @param escape 是否转义
     */
    public JsonQuery(String s, Boolean escape){
        if(escape){
            s = StringEscapeUtils.unescapeJava(s.trim());
        }
        s = s.trim();
        this.s = s;
        if (s.startsWith("{")) {
            m = toM(s);
        }else if (s.startsWith("[")) {
            l = toL(s);
        }
    }

    public Map<String, String> getM() {
        return m;
    }

    public List<String> getL() {
        return l;
    }

    public int m_size(){
        return m.size();
    }
    public int l_size(){
        return l.size();
    }
    public String ms(String key) {
        return m.get(key);
    }
    public JsonQuery m(String key) {
        return new JsonQuery(ms(key));
    }
    public Integer mi(String key) {
        return Integer.valueOf(ms(key));
    }
    public Double md(String key) {
        return Double.valueOf(ms(key));
    }
    public String ls(Integer index) {
        return l.get(index);
    }
    public JsonQuery l(Integer index) {
        return new JsonQuery(ls(index));
    }
    public Integer li(Integer index) {
        return Integer.valueOf(ls(index));
    }
    public Double ld(Integer index) {
        return Double.valueOf(ls(index));
    }
    public void put(String key, String value){
        m.put(key, value);
        StringBuilder sb = new StringBuilder(s);
        sb.insert(sb.lastIndexOf("}"), ",\"" + key + "\":\"" + value + "\"");
        s = sb.toString();
    }
    public void add(String value){
        l.add(value);
        StringBuilder sb = new StringBuilder(s);
        sb.insert(sb.lastIndexOf("]"), ",\"" + value);
        s = sb.toString();
    }
    /**
     * 注意ToM前需要保证{位置为0,这样代码里start等一才正确
     * @param json json字符串
     * @return Map<String, String> 解析后的格式
     */
    private Map<String, String> toM(String json) {
        if(json.length() > 2) {
            int start = 1;
            Integer end;
            while (start < json.length()) {
                end = IndexOf(json, start);
                if (end == json.length()) {
                    end = json.length() - 1;
                }
                String kv = json.substring(start, end);
                Integer colon = IndexOf(kv, 0, ':');
                String key = dropDoubleQuotation(kv.substring(0, colon));
                String value = dropDoubleQuotation(kv.substring(colon + 1));
                m.put(key, value);
                start = end + 1;
            }
        }
        return m;

    }

    private List<String> toL(String json) {
        if(json.length() > 2){
            int start = 1;
            Integer end;
            while (start < json.length()) {
                end = IndexOf(json, start);
                if(end == json.length()){ end = json.length() - 1; }
                String element = json.substring(start, end);
                if (element.startsWith("\"")){
                    element = dropDoubleQuotation(element);
                    l.add(element);
                }else {
                    l.add(element.trim());
                }
                start = end + 1;
            }
        }
        return l;
    }

    private String dropDoubleQuotation(String key) {
        key = key.trim();
        if (!key.startsWith("\"")) {
            return key;
        }
        return key.substring(1, key.length() - 1);
    }

    private Integer IndexOf(String json, Integer start) {
        return IndexOf(json, start, ',');
    }
    private Integer IndexOf(String json, Integer start, char split) {
        int brace = 0;
        int bracket = 0;
        int double_quotation_marks = 0;
        Integer length = json.length();
        for (; start < length; start++) {
            char c = json.charAt(start);
            if (c == split && brace == 0 && bracket == 0 && double_quotation_marks % 2 == 0) {
                break;
            }
            else if (c == '{' && double_quotation_marks % 2 == 0) {
                brace ++;
            } else if (c == '[' && double_quotation_marks % 2 == 0) {
                bracket ++;
            } else if (c == '"' && (start == 0 || (json.charAt(start - 1) != '\\' || (json.charAt(start - 1) == '\\' && json.charAt(start - 2) == '\\')))) {
                double_quotation_marks ++;
            } else if (c == '}' && double_quotation_marks % 2 == 0) {
                brace --;
            } else if (c == ']' && double_quotation_marks % 2 == 0) {
                bracket --;
            }

        }
        return start;
    }

    @Override
    public String toString() {
        return s;
    }

    public static void main(String[] args) {
        String s = " {\"employees\": [{\"firstName\": \"Bill\" ,\"lastName\": \"Gates\"},{\"firstName\": \"George\",\"lastName\": \"Bush\"}]}";
        JsonQuery j = new JsonQuery(s);
        System.out.println(j.m_size());
        JsonQuery e = j.m("employees");
        System.out.println(e.l(0).m("firstName"));
        for(String jq : e){
            System.out.println(jq);
        }
    }

    /**
     * @return 迭代器
     */
    public Iterator<String> iterator() {
        return this.l.iterator();
    }
}
