package index;

import java.util.LinkedList;

public class SendMessageTask implements Runnable {
	private Word word;
	private String file;
		
	public SendMessageTask(Word word, String file) {
		this.word = word;
		this.file = file;
	}
	
	@Override
	public void run() {
		PostDocumentData data = new PostDocumentData(file, word.termFrequency);
		LinkedList<PostDocumentData> documentData = new LinkedList<PostDocumentData>();
		documentData.add(data);
		
		PostDocumentData[] pd = new PostDocumentData[documentData.size()];
		pd = documentData.toArray(pd);
		
		Post entry = new Post(word.word, pd);
		
		Queue.publish(entry);
	}
	
}