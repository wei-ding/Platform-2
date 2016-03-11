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


import java.io.File
import javax.annotation.Nonnull

/**
 * Base trait that provides a path to an FSLuceneDirectory
 */
trait LuceneIndexPathProvider {
  /**
   * Provides the index directory path to the function f
   */
  protected def withIndexPath[T](f: (File) => T): T
}

/**
 * A LuceneIndexPathProvider that provides a directory path that is the
 * `index` sub-folder relative to the root of the service.
 * The sub-folder is created if it doesn't exist already.
 */
trait ServiceRootLucenePathProvider extends LuceneIndexPathProvider {
  private lazy val INDEX_DIRECTORY = "index"
  private lazy val serviceRootPath = System.getProperty("user.dir")
  private lazy val indexPath = new File(serviceRootPath, INDEX_DIRECTORY).getAbsolutePath

  @Nonnull
  protected def withIndexPath[T](@Nonnull f: (File) => T): T = {
    val indexFilePath = new File(indexPath)
    indexFilePath.mkdirs() // make sure path exist
    f(indexFilePath)
  }

}
