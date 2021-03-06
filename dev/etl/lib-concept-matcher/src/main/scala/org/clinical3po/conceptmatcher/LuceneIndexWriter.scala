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

package org.clinical3po.conceptmatcher


import org.apache.lucene.index.{IndexWriter, IndexWriterConfig}
import javax.annotation.Nonnull

/**
 * Provides a method to write to the index
 */
trait LuceneIndexWriter { self: LuceneDirectory with LuceneAnalyzerProvider =>

  /**
   * Calls the passed function with an IndexWriter that writes to the current index.
   * Makes sure to close the IndexWriter once the function returns.
   */
  @Nonnull
  def withIndexWriter[T](@Nonnull f: IndexWriter => T): T = {
    //val iwriter = new IndexWriter(directory, new IndexWriterConfig(luceneVersion, luceneAnalyzer))
    val iwriter = new IndexWriter(directory, false, new IndexWriterConfig(luceneAnalyzer))
    try {
      f(iwriter)
    } finally {
      iwriter.close()
    }
  }

}
