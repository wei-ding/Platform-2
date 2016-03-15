/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.clinical3po.namematcher

import org.apache.lucene.queryparser.classic.QueryParser
import javax.annotation.Nonnull
import org.apache.lucene.search.Query
import org.apache.lucene.document.Document

/**
 * Base trait for simple Lucene indexes
 * The index gets built once at construction
 */
trait ReadableLuceneIndex { self: LuceneDirectory with LuceneAnalyzerProvider =>

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
