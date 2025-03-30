package manager.solr.data;

import java.util.List;

public class SolrSearchRequest {
    public String searchInfo;

    /**
     * lucene
     * dismax
     * edismax
     */
    public String defType;

    /**
     * OR
     * AND
     */
    public String separatorMode;

    public Boolean sow;

    public Integer pageNum;
    public Boolean searchAllVersions;
    public List<String> searchVersions;
    public Integer fragSize;
    public Integer snippets;
    public Float minMatchAbility;
    public Boolean requireFieldMatch;
    public Boolean mergeContiguous;

    public Boolean highlightMultiTerm;
    public String fragListBuilder;
    public String boundaryScanner;

    public Boolean usePhraseHighlighter;


    public boolean isLucene () {
        return defType.equalsIgnoreCase("lucene");
    }

    public boolean isDismax () {
        return defType.equalsIgnoreCase("dismax");
    }

    public boolean isEdismax () {
        return defType.equalsIgnoreCase("edismax");
    }
}
