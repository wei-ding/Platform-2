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
import mlflex.helper.ListUtilities;
import mlflex.helper.MiscUtilities;
import mlflex.helper.ResultsFileUtilities;

import java.util.ArrayList;

/** This class provides functionality for handling data for the dependent/class variable. Normally a user would not specify this data processor in an experiment configuration file.
 * @author Stephen Piccolo
 */
public class DependentVariableDataProcessor extends AbstractDataProcessor
{
    /** Which data point should be used as the dependent variable. */
    public String DataPointName;

    /** Default constructor. */
    public DependentVariableDataProcessor() throws Exception
    {
        DataPointName = Singletons.Config.GetStringValue("DEPENDENT_VARIABLE_NAME", "Class");
    }

    @Override
    public String GetDescription()
    {
        return DataPointName;
    }

    /** This method saves basic statistical information about the transformed data used by this processor.
     *
     * @return Whether values were saved to the file system successfully
     * @throws Exception
     */
    public Boolean SaveStatistics() throws Exception
    {
        super.SaveStatistics();

        ArrayList<String> dependentVariableValues = new ArrayList<String>(Singletons.InstanceVault.DependentVariableInstances.values());

        ArrayList<NameValuePair> statistics = new ArrayList<NameValuePair>();

        for (String dependentVariableClass : Singletons.InstanceVault.DependentVariableOptions)
        {
            int numInstancesThisClass = ListUtilities.GetNumMatches(dependentVariableValues, dependentVariableClass);
            double proportionInstancesThisClass = numInstancesThisClass / (double)dependentVariableValues.size();

            statistics.add(NameValuePair.Create("Number [" + dependentVariableClass + "] instances", numInstancesThisClass));
            statistics.add(NameValuePair.Create("Proportion [" + dependentVariableClass + "] instances", proportionInstancesThisClass));
        }

        ResultsFileUtilities.AppendMatrixRows(statistics, Settings.GetOutputStatisticsDir() + GetDescription() + ".txt");

        return Boolean.TRUE;
    }
}
