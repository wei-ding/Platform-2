package gov.va.research.red;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Snippet {

	private String text;
	private Collection<LabeledSegment> labeledSegments;

	public Snippet(final String text, final Collection<LabeledSegment> labeledSegments) {
		this.text = text;
		this.labeledSegments = labeledSegments;
	}

	/**
	 * Copy constructor.
	 * @param snippet The Snippet to copy.
	 */
	public Snippet(Snippet snippet) {
		this.text = snippet.getText();
		this.labeledSegments = new ArrayList<LabeledSegment>(snippet.getLabeledSegments().size());
		for (LabeledSegment ls : snippet.getLabeledSegments()) {
			this.labeledSegments.add(new LabeledSegment(ls));
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Collection<LabeledSegment> getLabeledSegments() {
		if (labeledSegments == null) {
			labeledSegments = new ArrayList<LabeledSegment>();
		}
		return labeledSegments;
	}
	
	/**
	 * returns  the labeled segment with the parameter as the label.
	 * @param label The label of the labeled segment to return
	 * @return the first labeled segment with the <code>label</code>
	 */
	public LabeledSegment getLabeledSegment(String label) {
		if (labeledSegments == null) {
			return null;
		}
		for(LabeledSegment labelsegment : labeledSegments){
			if(labelsegment.getLabel() != null && labelsegment.getLabel().equalsIgnoreCase(label)){
				return labelsegment;
			}
		}
		return null;
	}

	public void setLabeledSegments(Collection<LabeledSegment> labeledSegments) {
		this.labeledSegments = labeledSegments;
	}

	public List<String> getLabeledStrings() {
		List<String> labeledStrings = new ArrayList<>();
		if (this.labeledSegments != null) {
			for (LabeledSegment ls : this.labeledSegments) {
				labeledStrings.add(ls.getLabeledString());
			}
		}
		return labeledStrings;
	}

	@Override
	public String toString() {
		return "" + text + " " + labeledSegments;
	}

}