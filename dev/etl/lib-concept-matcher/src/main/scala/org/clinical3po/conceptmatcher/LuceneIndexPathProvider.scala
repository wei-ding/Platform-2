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


import java.io.{File => JFile, FileSystem => JFileSystem, _} //TODO: Scala 2.10 does not like java.io._
import java.nio.file.Path 
import java.nio.file.Paths
import java.nio.file.Files
import javax.annotation.Nonnull

/**
 * Base trait that provides a path to an FSLuceneDirectory
 */
trait LuceneIndexPathProvider {
  /**
   * Provides the index directory path to the function f
   */
  protected def withIndexPath[T](f: (Path) => T): T
}

/** 
  * Base trait that provides the domains to a ServiceRootLucenePathProvider
  */
trait Domain
trait Lab extends Domain { override def toString() = "Lab" }
trait Drug extends Domain { override def toString() = "Drug" }
trait Condition extends Domain { override def toString() = "Condition" }
trait Procedure extends Domain { override def toString() = "Korean" }

/**
 * A LuceneIndexPathProvider that provides a directory path that is the
 * `index` sub-folder relative to the root of the service.
 * The sub-folder is created if it doesn't exist already.
 */
trait ServiceRootLucenePathProvider extends LuceneIndexPathProvider {
  private lazy val INDEX_DIRECTORY = "index"
  private lazy val serviceRootPath = System.getProperty("user.dir")
  private lazy val indexPath = new JFile(serviceRootPath, INDEX_DIRECTORY).getAbsolutePath

  @Nonnull
  protected def withIndexPath[T](@Nonnull f: (Path) => T): T = {
    val indexFilePath = new JFile(indexPath)
    try{    
        indexFilePath.mkdirs() // make sure path exist
    } catch {
        case ioe: IOException =>
            val sw = new StringWriter
            ioe.printStackTrace(new PrintWriter(sw))
            println(sw.toString)
        case e: Exception => 
            // TODO Auto-generated catch block
            e.printStackTrace();
    }

    f(Paths.get(indexFilePath.getAbsolutePath()))
  }

}
