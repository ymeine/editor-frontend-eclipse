package poc.outline;

import java.util.Map;

import org.eclipse.jface.viewers.LabelProvider;

public class POCOutlineLabelProvider extends LabelProvider {
	
	private static final String KEY_LABEL = "label";

	@Override
	public String getText(Object element) {
		return (String) Map.class.cast(element).get(KEY_LABEL);
	}

}
