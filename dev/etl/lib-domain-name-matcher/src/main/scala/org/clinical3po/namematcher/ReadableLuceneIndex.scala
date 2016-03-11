package com.gilt.lucene

import org.apache.lucene.queryparser.classic.QueryParser
import javax.annotation.Nonnull
import org.apache.lucene.search.Query
import org.apache.lucene.document.Document

/**
 * Base trait for simple Lucene indexes
 * The index gets built once at construction
 */
trait ReadableLuceneIndex extends LuceneVersion { self: LuceneDirectory with LuceneAnalyzerProvider =>

  /**
   * Returns a new QueryParser that defaults to the provided field
   */
  @Nonnull
  def queryParserForDefaultField(@Nonnull field: String) = new QueryParser(field, luceneAnalyzer)

  /**
   * Process a Lucene query string and returns the resulting documents
   */
  @Nonnull
  def searchTopDocuments(@Nonnull query: Query, @Nonnull limit: Int): Iterable[Document] = withIndexSearcher { indexSearcherOption =>
    indexSearcherOption.map { indexSearcher =>
      val hits = indexSearcher.search(query, limit)

      hits.scoreDocs.map { hit =>
        indexSearcher.doc(hit.doc)
      }.toIterable

    }.getOrElse(Iterable.empty)
  }

  /**
   * Returns a collection of all documents contained in the index
   */
  @Nonnull
  def allDocuments: Iterable[Document] = withDirectoryReader { directoryReaderOption =>
    directoryReaderOption.map { directoryReader =>
      (0 until directoryReader.numDocs).map(directoryReader.document)
    }.getOrElse(Iterable.empty)
  }

}
