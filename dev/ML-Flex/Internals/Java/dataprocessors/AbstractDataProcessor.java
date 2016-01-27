// THIS SOURCE CODE IS SUPPLIED "AS IS" WITHOUT WARRANTY OF ANY KIND, AND ITS AUTHOR AND THE JOURNAL OF MACHINE LEARNING RESEARCH (JMLR) AND JMLR'S PUBLISHERS AND DISTRIBUTORS, DISCLAIM ANY AND ALL WARRANTIES, INCLUDING BUT NOT LIMITED TO ANY IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND ANY WARRANTIES OR NON INFRINGEMENT. THE USER ASSUMES ALL LIABILITY AND RESPONSIBILITY FOR USE OF THIS SOURCE CODE, AND NEITHER THE AUTHOR NOR JMLR, NOR JMLR'S PUBLISHERS AND DISTRIBUTORS, WILL BE LIABLE FOR DAMAGES OF ANY KIND RESULTING FROM ITS USE. Without lim- iting the generality of the foregoing, neither the author, nor JMLR, nor JMLR's publishers and distributors, warrant that the Source Code will be error-free, will operate without interruption, or will meet the needs of the user.
// 
// --------------------------------------------------------------------------
// 
// Copyright 2016 Stephen Piccolo
// 
// This file is part of ML-Flex.
// 
// ML-Flex is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// any later version.
// 
// ML-Flex is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with ML-Flex. If not, see <http://www.gnu.org/licenses/>.

package mlflex.dataprocessors;

import mlflex.core.*;
import mlflex.parallelization.MultiThreadedTaskHandler;
import mlflex.summarization.GetFirstSummarizer;
//import mlflex.transformation.NullTransformer;
import mlflex.helper.*;

import java.util.*;
import java.util.concurrent.Callable;

/** This abstract class coordinates all tasks required to parse raw data, transform the data, and describe the data. This class takes care of the generic functionality to accomplish these tasks yet allows the user to develop custom classes that inherit from this class.
 *
 * @author Stephen Piccolo
 */
public abstract class AbstractDataProcessor
{
    private DataInstanceCollection _dataInstances = new DataInstanceCollection();

    /** This method takes the raw input data that the user specifies and stores it in the ML-Flex native format. It's necessary to store the files on disk to enable restartability.
     *
     * @return Whether the data was processed properly
     * @throws Exception
     */
    public Boolean ProcessInputData() throws Exception
    {
        Singletons.Log.Debug("Parsing input data for " + GetDescription());
        ParseInputData();

        return Boolean.TRUE;
    }

    /** When this method is executed, raw data is parsed and saved so it can be processed further. Classes that inherit from this class must implement this method if new data are being added to ML-Flex.
     *
     * @throws Exception
     */
    protected void ParseInputData() throws Exception
    {
        throw new Exception("Not implemented");
    }

    /** This method provides a description of the data provided by this processor. By default this description is the beginning of the class name.
     *
     * @return Description of the data
     */
    public String GetDescription()
    {
        return this.getClass().getSimpleName().replace("DataProcessor", "");
    }

    /** This method supports custom specification of how much sparsity is allowed per data instance. If a given data instance is missing more data than the specified proportion (0.00 - 1.00), it will be filtered out and not used in machine-learning analysis steps.
     *
     * @return Proportion of missing values allowed for a given data instance
     */
    protected double GetProportionMissingPerInstanceOK()
    {
        return 1.0;
    }

    /** This method supports custom specification of how much sparsity is allowed per data point. If a given data point is missing more data than the specified proportion (0.00 - 1.00) across all instances, it will be filtered out and not used in machine-learning analysis steps.
     *
     * @return Proportion of missing values allowed for a given data point
     */
    protected double GetProportionMissingPerDataPointOK()
    {
        return 1.0;
    }

    /** If the "PriorKnowledge" feature-selection approach is used, a hand-selected list of features for each data processor must be specified. In this case, this method should be overridden, and the values should be returned.
     *
     * @return A list of features that are believed (perhaps based on prior studies or a literature search) to be most relevant for classification.
     * @throws Exception
     */
    public ArrayList<String> GetPriorKnowledgeSelectedFeatures() throws Exception
    {
        return new ArrayList<String>();
    }

    /** This value provides DataValueMeta about a given data point. This helps ML-Flex to interpret the raw data as it is processed. If this is not override, generic metadata will be used.
     *
     * @param dataPointName Data point name
     * @return Metadata describing the data point.
     */
    public DataValueMeta GetDataPointMeta(String dataPointName)
    {
        return new DataValueMeta(dataPointName, new GetFirstSummarizer());
    }

    /** After raw data have been processed, they are packages into a DataInstanceCollection and can be processed further by calling this method.
     *
     * @return A collection of data instances
     * @throws Exception
     */
    public DataInstanceCollection GetDataInstances() throws Exception
    {
        return _dataInstances;
    }

    /** Retrieves data instances from a processed file.
     *
     * @return Data instances that are stored in an ML-Flex formatted file
     * @throws Exception
     */
//    protected DataInstanceCollection GetInstancesFromFile() throws Exception
//    {
//        String filePath = GetDataFilePath();
//
//        if (!FileUtilities.FileExists(filePath))
//            Singletons.Log.ExceptionFatal("No data file exists at " + filePath + ".");
//
//        return DataInstanceCollection.DeserializeFromFile(filePath);
//    }

//    /** After data instances have been processed and stored, it is still possible to modify them before each time they are used in a machine-learning analysis. This method supports that functionality.
//     *
//     * @param instances Data instances to be updated
//     * @throws Exception
//     */
//    public void UpdateInstancesForAnalysis(DataInstanceCollection instances) throws Exception
//    {
//    }

    /** Returns the absolute file path where ML-Flex stores data for this processor.
     *
     * @return Absolute file path where ML-Flex stores data file
     */
    protected String GetDataFilePath()
    {
        return Settings.DATA_DIR + GetDescription() + ".data";
    }

    /** This method is used by most custom data processors that inherits from AbstractDataProcessor. It stores a given raw data point for further processing.
     *
     * @param dataPointName The name that should be used by ML-Flex to describe the data value
     * @param instanceID The instance ID associated witih the data value
     * @param value The data value
     * @throws Exception
     */
    public void SaveRawDataPoint(String dataPointName, String instanceID, String value) throws Exception
    {
        _dataInstances.Add(dataPointName, instanceID, value);
    }

    /** This method saves basic statistical information about the transformed data used by this processor.
     *
     * @return Whether values were saved to the file system successfully
     * @throws Exception
     */
    public Boolean SaveStatistics() throws Exception
    {
        DataInstanceCollection dataInstances = Singletons.InstanceVault.GetInstancesForAnalysis(this);

        if (dataInstances != null)
        {
            ArrayList<NameValuePair> statistics = new ArrayList<NameValuePair>();

            statistics.add(new NameValuePair("Statistic", "Value"));
            statistics.add(NameValuePair.Create("Num Instances", dataInstances.Size()));
            statistics.add(NameValuePair.Create("Num Variables", dataInstances.GetNumDataPoints()));
            statistics.add(NameValuePair.Create("Proportion Missing Values", dataInstances.GetProportionMissingValues()));

            ResultsFileUtilities.AppendMatrixColumn(statistics, GetStatisticsFilePath(), " Summary statistics describing input data.");
        }

        return Boolean.TRUE;
    }

    /** This method indicates where the statistics will be stored for this data processor.
     *
     * @return Path to the file containing statistics for this processor
     * @throws Exception
     */
    public String GetStatisticsFilePath() throws Exception
    {
        return Settings.GetOutputStatisticsDir() + GetDescription() + ".txt";
    }

    /** This method saves basic statistical information that describes all independent-variable processors.
     *
     * @return Whether the save was successful
     * @throws Exception
     */
    public static Boolean SaveStatisticsAcrossAllIndependentVariableProcessors() throws Exception
    {
        ArrayList<String> allIDs = new ArrayList<String>();

        for (AbstractDataProcessor processor : Singletons.ProcessorVault.IndependentVariableDataProcessors)
            allIDs.addAll(Singletons.InstanceVault.GetInstanceIDsForAnalysis(processor));

        // The following code produces hash maps that tell how often instances fall into each data processor
        HashMap<String, Integer> idFrequencyMap = MapUtilities.GetFrequencyMap(allIDs);
        ArrayList<String> allFrequencies = ListUtilities.CreateStringList(idFrequencyMap.values());
        HashMap<String, Integer> frequencyFrequencyMap = MapUtilities.GetFrequencyMap(allFrequencies);

        ArrayList<NameValuePair> statistics = new ArrayList<NameValuePair>();

        for (Map.Entry<String, Integer> entry : frequencyFrequencyMap.entrySet())
            statistics.add(NameValuePair.Create("Subjects with data for at least " + entry.getKey() + " data category", MapUtilities.GetNumKeysGreaterThanOrEqualTo(frequencyFrequencyMap, Integer.parseInt(entry.getKey()))));

        ResultsFileUtilities.AppendMatrixColumn(statistics, Settings.GetOutputStatisticsDir() + "All.txt", "Summary statistics describing all independent variable data.");

        return Boolean.TRUE;
    }

    /** This method indicates whether this instance is equal to another instance, based on the descriptions.
     *
     * @param obj Object to be tested
     * @return Whether this instance is equal to another instance
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj == null) return false;
        if (!(obj instanceof AbstractDataProcessor)) return false;

        AbstractDataProcessor compareObj = (AbstractDataProcessor)obj;
        return compareObj.GetDescription().equals(this.GetDescription());
    }

    @Override
    public int hashCode()
    {
        return this.GetDescription().hashCode();
    }

    /** String representation of this class.
     *
     * @return Description
     */
    @Override
    public String toString()
    {
        return GetDescription();
    }
}