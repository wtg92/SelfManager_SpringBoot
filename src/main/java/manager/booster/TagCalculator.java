package manager.booster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import manager.data.EntityTag;
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
	
	final private static String SEPARATOR = ";;;";
	
	final private static String CREATED_BY_SYSTEM ="^^^";
	
	
	public static String mergeTags(List<EntityTag> tags) {
		return tags.stream().map(TagCalculator::parseTo).collect(Collectors.joining(SEPARATOR));
	}
	
	public static String parseTo(EntityTag tag) {
		return tag.createdBySystem ? CREATED_BY_SYSTEM+tag.name : tag.name;
	}
	
	public static EntityTag parseTo(String tagStr) {
		EntityTag tag = new EntityTag();
		
		boolean createdBySystem = tagStr.startsWith(CREATED_BY_SYSTEM);
		tag.createdBySystem = createdBySystem;
		if(!createdBySystem) {
			tag.name = tagStr;
			return tag;
		}
		
		tag.name = tagStr.substring(CREATED_BY_SYSTEM.length());
		
		return tag;
	}
	
	public static List<EntityTag> parseToTags(String tagsStr){
		if(tagsStr == null) {
			return new ArrayList<>();
		}
		
		return Arrays.stream(tagsStr.split(SEPARATOR)).filter(e-> !e.trim().isEmpty())
				.map(TagCalculator::parseTo).collect(Collectors.toList());
	}
	
	public static void checkTagsForReset(List<EntityTag> tags) throws LogicException {
		List<String> tagNames = tags.stream().map(tag->tag.name).collect(Collectors.toList());
		for(String tag:tagNames) {
			checkTagLegal(tag);
		}
		checkTagsUnique(tagNames);
	}

	private static void checkTagLegal(String tag) throws LogicException {
		if(tag.contains(SEPARATOR)) {
			throw new LogicException(SMError.ILLEGAL_TAG, SEPARATOR);
		}
		if(tag.contains(CREATED_BY_SYSTEM)) {
			throw new LogicException(SMError.ILLEGAL_TAG,CREATED_BY_SYSTEM);
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
