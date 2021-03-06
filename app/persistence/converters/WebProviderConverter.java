package persistence.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import datadefinitions.WebProvider;


@Converter(autoApply=true)
public class WebProviderConverter implements AttributeConverter<WebProvider, Integer>{

	@Override
	public Integer convertToDatabaseColumn(WebProvider wp) {
		return 0;
	}

	@Override
	public WebProvider convertToEntityAttribute(Integer id) {
		return WebProvider.ADVANCED_AUTO_DEALERS;
	}

}