package org.dlac.tubekits;

import org.apache.commons.lang.StringEscapeUtils;

public class PlayItem {
	
	public static enum ItemType {
		SEARCH,
		LINK,
		PLAYLIST
	};
	
	public ItemType type;
	public String keyword;
	public String url;
	public Boolean willLikeOrDislike;
	public String comment;
	public Boolean loop;
	public Boolean shuffle;
	public Boolean autoNext;
	
	public PlayItem(ItemType type,
					String keyword,
					String url,
					Boolean willLikeOrDislike,
					String comment,
					Boolean loop,
					Boolean shuffle,
					Boolean autoNext)
	{
		this.type = type;
		this.keyword = keyword;
		this.url = url;
		this.willLikeOrDislike = willLikeOrDislike;
		this.comment = comment;
		this.loop = loop;
		this.shuffle = shuffle;
		this.autoNext = autoNext;
	}
	
	public static PlayItem createKeywordItem(String keyword, Boolean willLikeOrDislike, String comment) {
		return new PlayItem(ItemType.SEARCH, keyword, null, willLikeOrDislike, comment, null, null, null);
	}
	
	public static PlayItem createLinkItem(String url, Boolean willLikeOrDislike, String comment) {
		return new PlayItem(ItemType.LINK, null, url, willLikeOrDislike, comment, null, null, null);
	}
	
	public static PlayItem createPlaylistItem(String url, Boolean loop, Boolean shuffle, Boolean autoNext) {
		return new PlayItem(ItemType.PLAYLIST, null, url, null, null, loop, shuffle, autoNext);
	}
	
	public String toJSON() {
		if (type == ItemType.SEARCH) {
			return "{ \"type\": \"search\", "
					+ "\"keyword\": \"" + StringEscapeUtils.escapeJava(keyword) + "\", "
					+ "\"willLikeOrDislike\": " + willLikeOrDislike.toString() + ", "
					+ "\"comment\": " + ((comment == null) ? ("null") : ("\"" + StringEscapeUtils.escapeJava(comment) + "\""))
					+ " }";
		}
		
		if (type == ItemType.LINK) {
			return "{ \"type\": \"link\", "
					+ "\"url\": \"" + url + "\", "
					+ "\"willLikeOrDislike\": " + willLikeOrDislike.toString() + ", "
					+ "\"comment\": " + ((comment == null) ? ("null") : ("\"" + StringEscapeUtils.escapeJava(comment) + "\""))
					+ " }";
		}
		
		if (type == ItemType.PLAYLIST) {
			return "{ \"type\": \"playlist\", "
					+ "\"url\": \"" + url + "\", "
					+ "\"loop\": " + loop.toString() + ", "
					+ "\"shuffle\": " + shuffle.toString() + ", "
					+ "\"autoNext\": " + autoNext.toString()
					+ " }";
		}
		
		return "null";
	}
}
