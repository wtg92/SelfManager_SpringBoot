package manager.logic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import manager.exception.LogicException;
import manager.system.SMError;

/**
 * 该类旨在为系统内可能出现给实体打标签行为，提供统一的管理方式。
 * 思路：实体增加字段tags。
 * tags可以管理多个标签，使用;;;分隔。
 * 标签不可以包含分隔字符
 * 
 * @author 王天戈
 *
 */
public abstract class TagCalculator {
	
	final private static Logger logger = Logger.getLogger(TagCalculator.class.getName());
	
	final private static String SEPERATOR = ";;;";
	
	public static String mergeTags(List<String> tags) {
		return tags.stream().collect(Collectors.joining(SEPERATOR));
	}
	
	public static List<String> parseToTags(String tagsStr){
		if(tagsStr == null) {
			return new ArrayList<String>();
		}
		
		return new ArrayList<String>(Arrays.asList(tagsStr.split(SEPERATOR)));
	}
	
	public static void checkTagsForReset(List<String> tags) throws LogicException {
		for(String tag:tags) {
			checkTagLegal(tag);
		}
		checkTagsUnique(tags);
	}

	public static List<String> addTag(String tagForAdd,List<String> existedTags) throws LogicException {
		
		checkTagLegal(tagForAdd);
		
		existedTags.add(tagForAdd);
		
		checkTagsUnique(existedTags);
		
		return existedTags;
	}
	
	public static List<String> deleteTag(String tagForDelete,List<String> existedTags) throws LogicException {
		boolean contains = existedTags.remove(tagForDelete);
		if(!contains) {
			logger.log(Level.WARNING, "删除了不存在的Tag " +tagForDelete);
		}
		return existedTags;
	}
	
	private static void checkTagLegal(String tag) throws LogicException {
		if(tag.contains(SEPERATOR)) {
			throw new LogicException(SMError.ILLEGAL_TAG,SEPERATOR);
		}
	}
	
	private static void checkTagsUnique(List<String> tags) throws LogicException {
		Map<String, Long> countGroupBy = tags.stream().collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));
		
		List<String> dupTags = new ArrayList<String>();
		countGroupBy.forEach((tag,count)->{
			if(count > 1) {
				dupTags.add(tag);
			}
		});
		
		if(dupTags.size() == 0) {
			return;
		}
		
		throw new LogicException(SMError.DUP_TAG,dupTags.stream().collect(Collectors.joining(",")));
	}
}
