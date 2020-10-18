package application.domain.history_doc_generator;

import java.io.IOException;

public interface IHistoryDocGenerator {
    byte[] getDoc() throws IOException;
}
