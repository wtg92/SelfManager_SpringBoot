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

    public String mm;

    public Boolean mmAutoRelax;

    public String hlMethod;

    public Boolean applyHighlighting;

    public Integer fragSize;
    public Integer snippets;
    public Boolean requireFieldMatch;

    public Boolean highlightMultiTerm;

    public Boolean usePhraseHighlighter;

    public Boolean hlFragsizeIsMinimum;

    public String hlTagEllipsis;

    public String hlScoreK1;

    public String hlScoreB;

    public Integer hlScorePivot;

    public Boolean hlWeightMatches;

    public Boolean mergeContiguous;


    public boolean isUnifiedHL(){
        return hlMethod.equalsIgnoreCase("unified");
    }

    public boolean isOriginalHL(){
        return hlMethod.equalsIgnoreCase("original");
    }


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
