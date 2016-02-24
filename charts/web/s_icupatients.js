//# 
'use strict';


var ageRingChart   = dw.pieChart("#chart-ring-age"),
    genderChart  = dw.pieChart("#chart-pie-gender"),
    icutypeChart = dw.pieChart("#chart-pie-icutype"),
    survivalChart  = dw.pieChart("#chart-pie-survival"),
    deathChart = dw.pieChart("#chart-pie-death"),
    bmiChart = dw.lineChart("#chart-area-bmi"),
    bmiPieChart = dw.pieChart("#chart-pie-bmi"),
    bpChart = dw.lineChart("#chart-area-bp"),
    bpCategoryChart = dw.rowChart("#chart-pie-bp"),
    albuminChart = dw.lineChart("#chart-area-albumin"),
    albuminCategoryChart = dw.pieChart("#chart-pie-albumin"),
    alpChart = dw.lineChart("#chart-area-alp"),
    alpCategoryChart = dw.pieChart("#chart-pie-alp");

var altChart = dw.lineChart("#chart-area-alt");
var altCategoryChart = dw.pieChart("#chart-pie-alt");

var astChart = dw.lineChart("#chart-area-ast");
var astCategoryChart = dw.pieChart("#chart-pie-ast");

// Bilirubin
var BilirubinChart = dw.lineChart("#chart-area-Bilirubin");
var BilirubinCategoryChart = dw.pieChart("#chart-pie-Bilirubin");

// BUN 
var bunChart = dw.lineChart("#chart-area-bun");
var bunCategoryChart = dw.pieChart("#chart-pie-bun");

// Cholesterol
var CholesterolChart = dw.lineChart("#chart-area-Cholesterol");
var CholesterolCategoryChart = dw.pieChart("#chart-pie-Cholesterol");

// Creatinine 
var CreatinineChart = dw.lineChart("#chart-area-Creatinine");
var CreatinineCategoryChart = dw.pieChart("#chart-pie-Creatinine");

// Glucose 
var GlucoseChart = dw.lineChart("#chart-area-Glucose");
var GlucoseCategoryChart = dw.pieChart("#chart-pie-Glucose");

// HCO3 / Serum Bicarbonate 
var HCO3Chart = dw.lineChart("#chart-area-HCO3");
var HCO3CategoryChart = dw.pieChart("#chart-pie-HCO3");

// HCT 
var HCTChart = dw.lineChart("#chart-area-HCT");
var HCTCategoryChart = dw.pieChart("#chart-pie-HCT");

// K Serum Potassium 
var KChart = dw.lineChart("#chart-area-K");
var KCategoryChart = dw.pieChart("#chart-pie-K");
    
// Lactate
var LactateChart = dw.lineChart("#chart-area-Lactate");
var LactateCategoryChart = dw.pieChart("#chart-pie-Lactate");

// Serum magnesium 
var MgChart = dw.lineChart("#chart-area-Mg");
var MgCategoryChart = dw.pieChart("#chart-pie-Mg");
    
// Mechanical ventilation respiration 
var MechVentCategoryChart = dw.pieChart("#chart-pie-MechVent");

// Serum Sodium 
var NaChart = dw.lineChart("#chart-area-Na");
var NaCategoryChart = dw.pieChart("#chart-pie-Na");

// PaCO2
var PaCO2Chart = dw.lineChart("#chart-area-PaCO2");
var PaCO2CategoryChart = dw.pieChart("#chart-pie-PaCO2");

// PaO2 
var PaO2Chart = dw.lineChart("#chart-area-PaO2");
var PaO2CategoryChart = dw.pieChart("#chart-pie-PaO2");

// pH 
var pHChart = dw.lineChart("#chart-area-pH");
var pHCategoryChart = dw.pieChart("#chart-pie-pH");

// Platelets
var PlateletsChart = dw.lineChart("#chart-area-Platelets");
var PlateletsCategoryChart = dw.pieChart("#chart-pie-Platelets");

// RespRate  
var RespRateChart = dw.lineChart("#chart-area-RespRate");
var RespRateCategoryChart = dw.pieChart("#chart-pie-RespRate");

// SaO2 
var SaO2Chart = dw.lineChart("#chart-area-SaO2");
var SaO2CategoryChart = dw.pieChart("#chart-pie-SaO2");

// Temp 
var TempChart = dw.lineChart("#chart-area-Temp");
var TempCategoryChart = dw.pieChart("#chart-pie-Temp");

// Urine 
var UrineChart = dw.lineChart("#chart-area-Urine");
var UrineCategoryChart = dw.pieChart("#chart-pie-Urine");

// WBC 
var WBCChart = dw.lineChart("#chart-area-WBC");
var WBCCategoryChart = dw.pieChart("#chart-pie-WBC");

d3.csv("data/ptsummary.csv", function(error, psummary) {
  // normalize/parse data
  var dateFormat = d3.time.format("%m/%d/%Y");
  var numberFormat = d3.format(".1f");
  //
  // normalize/parse data
  psummary.forEach(function(d) {
      if ( d.first_bmi > 10 && d.first_bmi < 100 )
        d.first_bmi = d3.round(d.first_bmi,1);
      else 
        d.first_bmi = 0;
      d.NIMAP = d.NIMAP ? Math.round(d.NIMAP) : 0;
      d.NISysABP = d.NISysABP ? Math.round(d.NISysABP) : 0;
      d.NIDiasABP = d.NIDiasABP ? Math.round(d.NIDiasABP) : 0;
      d.Albumin  =  d.Albumin ? d3.round(d.Albumin,2) : 0;
      d.ALP =  d.ALP ? d3.round(d.ALP,2) : 0;
      d.ALT =  d.ALT ? d3.round(d.ALT,2) : 0;
      d.AST =  d.AST ? d3.round(d.AST,2) : 0;
      d.Bilirubin = d.Bilirubin ? d3.round(d.Bilirubin,2) : 0;
      d.BUN = d.BUN ? d3.round(d.BUN,2) : 0;
      d.Cholesterol = d.Cholesterol ? d3.round(d.Cholesterol,2) : 0;
      d.Creatinine = d.Creatinine? d3.round(d.Creatinine,2) : 0;

      d.Glucose = d.Glucose ? d3.round(d.Glucose,2) : 0;

      d.HCO3 = d.HCO3 ? Math.round(d.HCO3) : 0;

      d.HCT = d.HCT ? Math.round(d.HCT) : 0;
      d.HCT = d.HCT > 100 && d.HCT < 1000 ? Math.round(d.HCT / 10) : d.HCT;

      d.K = d.K ? d3.round(d.K,2) : 0;

      d.Lactate = d.Lactate ? d3.round(d.Lactate,2) : 0;

      d.Mg = d.Mg ? d3.round(d.Mg,2) : 0;

      d.MechVent = d.MechVent ? Math.round(d.MechVent) : 0;

      d.Na = d.Na ? Math.round(d.Na) : 0;

      d.PaCO2 = d.PaCO2 ? Math.round(d.PaCO2) : 0;

      d.PaO2 = d.PaO2 ? Math.round(d.PaO2) : 0;

      d.pH = d.pH ? d3.round(d.pH, 2) : 0;

      d.Platelets = d.Platelets ? Math.round(d.Platelets) : 0;

      d.RespRate = d.RespRate ? Math.round(d.RespRate) : 0;

      d.SaO2 = d.SaO2 ? Math.round(d.SaO2) : 0;

      d.Temp = d.Temp ? Math.round(d.Temp, 2) : 0;

      d.Urine = d.Urine ? Math.round(d.Urine) : 0;

      d.WBC = d.WBC ? d3.round(d.WBC, 2) : 0;
  });
  // set crossfilter
  var ndx = crossfilter(psummary);
  var all = ndx.groupAll();

  var ageDim  = ndx.dimension(function(d) {
          var age = d.age;
          if ( age > 19 &&  age < 30)
              return "20s";
          else if (age > 29 && age < 40)
              return "30s";
          else if (age > 39 && age < 50)
              return "40s";
          else if (age > 49 && age < 60)
              return "50s";
          else if (age > 59 && age < 70)
              return "60s";
          else if (age > 69 && age < 80)
              return "70s";
          else if (age > 79 && age < 90)
              return "80s";
          else if (age > 89 && age < 100)
              return "90s";
          else if (age > 99)
              return "100s";
          else
              return "Teenager";
      }),

      genderDim  = ndx.dimension(function(d) {
        var gender = d.gender;
        if ( gender == "1" )
            return "M";
        else if ( gender == "0" )
          return "F";
        else 
          return "O";
       }),

      icutypeDim  = ndx.dimension(function(d) {
        var icu = d.icu_type;
        if ( icu == "1" )
            return "CCU";
        else if ( icu == "2" )
            return "CSRU";
        else if ( icu == "3" )
            return "Medical";
        else if ( icu == "4" )
            return "Surgical";
        else 
            return "Other";

      }),

      survivalDim = ndx.dimension(function(d) {
        if ( d.survival > d.los || d.survival == "-1" )
            return "Y";
        else if ( d.survival >= 2 && d.survival <= d.los ) 
          return "N";
        else 
          return "Other";
       }),


      deathDim = ndx.dimension(function(d) {
          return d.in_hosp_death == "1" ? "Y" : "N";
      }),

      bmiDim = ndx.dimension(function(d) {
          return d.first_bmi;
      }),


      bmiPieDim = ndx.dimension(function(d) {
        var bmi = d.first_bmi;
        if ( (bmi > 10 && bmi < 20 && d.gender == 1 ) || (bmi > 10 && bmi < 19 && d.gender == 0) )
            return "Under";
        else if ( (bmi >= 20 && bmi <=25 && d.gender == 1) || (bmi >= 19 && bmi <=25 && d.gender == 0) )
            return "Normal";
        else if ( bmi >= 25 && bmi <= 30 )
            return "Over";
        else if ( bmi >= 30 && bmi <= 40 )
            return "Obese";
        else if ( bmi > 40 && bmi <= 100 )
            return "Morbidly";
      }),


      bpDim = ndx.dimension(function(d) {
          return d.NIMAP;
      }),
      bpPieDim = ndx.dimension(function(d) {
        if ( d.NISysABP < 90 && d.NIDiasABP < 60 )
            return "Low";
        else if ( d.NISysABP >= 90 && d.NISysABP < 120 && d.NIDiasABP >= 60 && d.NIDiasABP < 80 )
            return "Normal";
        else if ( ( d.NISysABP >= 120 && d.NISysABP <= 139 ) || ( d.NIDiasABP >= 80 && d.NIDiasABP <= 89) )
            return "Pre HTN";
        else if ( ( d.NISysABP >= 140 && d.NISysABP <= 159 ) || ( d.NIDiasABP >= 90 && d.NIDiasABP <= 99) )
            return "HTN Stage1";
        else if ( ( d.NISysABP >= 160 && d.NISysABP <= 180 ) || ( d.NIDiasABP >= 100 && d.NIDiasABP <= 110) )
            return "HTN Stage2";
        else if ( d.NISysABP > 180 || d.NIDiasABP > 110 ) 
            return "HTN Crisis";
        else 
          return "Other";
      }),

      albuminDim = ndx.dimension(function(d) {
          return d.Albumin;
      }),

      albuminCategoryDim = ndx.dimension(function(d) {
        if ( d.Albumin > 0 && d.Albumin < 3.4 )
            return "Low";
        else if ( d.Albumin > 5.4 ) 
          return "High";
        else if ( d.Albumin >= 3.4 && d.Albumin <= 5.4 )
          return "Normal";
        else 
          return "None";
      }),

      alpDim = ndx.dimension(function(d) {
          return d.ALP;
      }),

      alpCategoryDim = ndx.dimension(function(d) {
        if ( d.ALP > 0 && d.ALP < 44 )
            return "Low";
        else if ( d.ALP > 147 ) 
            return "High";
        else if ( d.ALP >= 44 && d.ALP <= 147 ) 
            return "Normal";
        else 
            return "None";
      }),

      altDim = ndx.dimension(function(d) {
          return d.ALT;
      }),

      altCategoryDim = ndx.dimension(function(d) {
        if ( (d.gender == 1 && d.ALT > 0 && d.ALT < 10) || ( d.gender == 0 && d.ALT > 0 && d.ALT < 7 ) )
            return "Low";
        else if ( (d.gender == 1 && d.ALT > 40 ) || ( d.gender == 0 && d.ALT > 35 ))
            return "High";
        else if ( (d.gender == 1 && d.ALT >= 10 && d.ALT <= 40 ) || ( d.gender == 0 && d.ALT >= 7 && d.ALT <= 35 ))
            return "Normal";
        else 
            return "None";
      }),

      astDim = ndx.dimension(function(d) {
          return d.AST;
      }),

      astCategoryDim = ndx.dimension(function(d) {
        if ( (d.gender == 1 && d.AST > 0 && d.AST < 14 ) || ( d.gender == 0 && d.AST > 0 && d.AST < 10 ) )
            return "Low";
        else if ( (d.gender == 1 && d.AST > 20 ) || ( d.gender == 0 && d.AST > 36 ))
            return "High";
        else if ( (d.gender == 1 && d.AST >= 14 && d.AST <= 20 ) || ( d.gender == 0 && d.AST >= 10 && d.AST <= 36 ))
            return "Normal";
        else 
            return "None";
      }),
      
      // Bilirubin 
      BilirubinDim = ndx.dimension(function(d) {
          return d.Bilirubin;
      }),

      BilirubinCategoryDim = ndx.dimension(function(d) {
        if ( d.Bilirubin > 0 && d.Bilirubin < 0.1 )
            return "Low";
        else if ( d.Bilirubin > 1.2 )
            return "High";
        else if ( d.Bilirubin >= 0.1 && d.Bilirubin <= 1.2 )
            return "Normal";
        else 
            return "None";
      }),
      
      // BUN 
      bunDim = ndx.dimension(function(d) {
          return d.BUN;
      }),

      bunCategoryDim = ndx.dimension(function(d) {
        if ( d.BUN > 0 && d.BUN < 8 )
            return "Low";
        else if ( d.BUN > 20)
            return "High";
        else if ( d.BUN >= 8 && d.BUN <= 20 )
            return "Normal";
        else 
            return "None";
      }),
      
      // Cholesterol
      CholesterolDim = ndx.dimension(function(d) {
          return d.Cholesterol;
      }),

      CholesterolCategoryDim = ndx.dimension(function(d) {
        if ( d.Cholesterol > 0 && d.Cholesterol < 200 )
            return "Desirable";
        else if ( d.Cholesterol >= 200 && d.Cholesterol <= 239 )
            return "Borderline";
        else if ( d.Cholesterol > 240 )
            return "High Risk";
        else 
            return "None";
      }),

      // Creatinine 
      CreatinineDim = ndx.dimension(function(d) {
          return d.Creatinine;
      }),

      CreatinineCategoryDim = ndx.dimension(function(d) {
        if ( d.Creatinine > 0 && d.Creatinine < 0.6 )
            return "Low";
        else if ( d.Creatinine > 1.2 )
            return "High";
        else if ( d.Creatinine >=0.6 && d.Creatinine <= 1.2 )
            return "Normal";
        else 
            return "None";
      }),
     
      // Glucose 
      GlucoseDim = ndx.dimension(function(d) {
          return d.Glucose;
      }),

      GlucoseCategoryDim = ndx.dimension(function(d) {
        if ( d.Glucose > 0 && d.Glucose < 60 )
            return "Low";
        else if ( d.Glucose > 115 )
            return "High";
        else if ( d.Glucose >= 60 && d.Glucose <= 115 )
            return "Normal";
        else 
            return "None";
      }),

      // HCO3 Serum bicarbonate 
      HCO3Dim = ndx.dimension(function(d) {
          return d.HCO3;
      }),

      HCO3CategoryDim = ndx.dimension(function(d) {
        if ( d.HCO3 > 0 && d.HCO3 < 22 )
            return "Low";
        else if ( d.HCO3 > 26 )
            return "High";
        else if ( d.HCO3 >= 22 && d.HCO3 <= 26 )
            return "Normal";
        else 
            return "None";
      }),

      // HCT
      HCTDim = ndx.dimension(function(d) {
          return d.HCT;
      }),

      /* 
        Hematocrit (Hct) Levels. This is the ratio of the volume of red cells 
        to the volume of whole blood. Normal range for hematocrit is different 
        between the sexes and is approximately 45% to 52% for men and 37% to 48% for women.
      */
      HCTCategoryDim = ndx.dimension(function(d) {
        if ( d.HCT > 0 && (( d.gender == 1 && d.HCT <45 ) || ( d.gender == 0 && d.HCT > 37 )))
            return "Low";
        else if (( d.gender == 1 && d.HCT > 52 ) || ( d.gender == 0 && d.HCT > 48 ))
            return "High";
        else if (( d.gender == 1 && d.HCT >= 45 && d.HCT <= 52 ) || ( d.gender == 0 && d.HCT >= 37 && d.HCT <= 48 ))
            return "Normal";
        else 
            return "None";
      });

      ageDim.dispose();
      genderDim.dispose();
      icutypeDim.dispose();
      survivalDim.dispose();
      deathDim.dispose();
      bmiDim.dispose();
      bmiPieDim.dispose();
      bpDim.dispose();
      bpPieDim.dispose();
      albuminDim.dispose();
      albuminCategoryDim.dispose();
      alpDim.dispose();
      alpCategoryDim.dispose();
      altDim.dispose();
      altCategoryDim.dispose();
      astDim.dispose();
      astCategoryDim.dispose();
      BilirubinDim.dispose();
      BilirubinCategoryDim.dispose();
      bunDim.dispose();
      bunCategoryDim.dispose();
      CholesterolDim.dispose();
      CholesterolCategoryDim.dispose();
      CreatinineDim.dispose();
      CreatinineCategoryDim.dispose();
      GlucoseDim.dispose();
      GlucoseCategoryDim.dispose();
      HCO3Dim.dispose();
      HCO3CategoryDim.dispose();
      HCTDim.dispose();
      HCTCategoryDim.dispose();
      
      // K 
      var KDim = ndx.dimension(function(d) {
          return d.K;
      }),

      /* 
      */
      KCategoryDim = ndx.dimension(function(d) {
        if ( d.K > 0 && d.K < 3.5 )
            return "Low";
        else if ( d.K > 5.5 )
            return "High";
        else if ( d.K >= 3.5 && d.K <=5.5 )
            return "Normal";
        else 
            return "None";
      });

      KDim.dispose();
      KCategoryDim.dispose();
      
      
      // Lactate
      var LactateDim = ndx.dimension(function(d) {
          return d.Lactate;
      });
  

      var LactateCategoryDim = ndx.dimension(function(d) {
        if ( d.Lactate > 0 && d.Lactate < 0.5 )
            return "Low";
        else if ( d.Lactate > 2.2 )
            return "High";
        else if ( d.Lactate >= 0.5 && d.Lactate <= 2.2 )
            return "Normal";
        else 
            return "None";
      });

      LactateDim.dispose();
      LactateCategoryDim.dispose();

      // Serum magnesium 
      var MgDim = ndx.dimension(function(d) {
          return d.Mg;
      });
  

      // Serum Sodim
      var MgCategoryDim = ndx.dimension(function(d) {
        if ( d.Mg> 0 && d.Mg < 1.5 )
            return "Low";
        else if ( d.Lactate > 2.5 )
            return "High";
        else if ( d.Lactate >= 1.5 && d.Lactate <= 2.5 )
            return "Normal";
        else 
            return "None";
      });

      MgDim.dispose();
      MgCategoryDim.dispose();

      // Mechanical ventilation respiration 
      var MechVentCategoryDim = ndx.dimension(function(d) {
          return d.MechVent == 1 ? "Yes" : "No";
      });

      MechVentCategoryDim.dispose();

      // Serum Sodium 
      var NaDim = ndx.dimension(function(d) {
          return d.Na;
      });
  

      var NaCategoryDim = ndx.dimension(function(d) {
        if ( d.Na> 0 && d.Na < 135 )
            return "Low";
        else if ( d.Na > 145 )
            return "High";
        else if ( d.Na >= 135 && d.Na <= 145 )
            return "Normal";
        else 
            return "None";
      });

      NaDim.dispose();
      NaCategoryDim.dispose();

      // PaCO2 
      var PaCO2Dim = ndx.dimension(function(d) {
          return d.PaCO2;
      });
  

      var PaCO2CategoryDim = ndx.dimension(function(d) {
        if ( d.PaCO2 > 0 && d.PaCO2 < 35 )
            return "Low";
        else if ( d.PaCO2 > 45 )
            return "High";
        else if ( d.PaCO2 >= 35 && d.PaCO2 <= 45 )
            return "Normal";
        else 
            return "None";
      });

      PaCO2Dim.dispose();
      PaCO2CategoryDim.dispose();
      
      // PaO2
      var PaO2Dim = ndx.dimension(function(d) {
          return d.PaO2;
      });
  

      var PaO2CategoryDim = ndx.dimension(function(d) {
        if ( d.PaO2 > 0 && d.PaO2 < 80 )
            return "Low";
        else if ( d.PaO2 > 100 )
            return "High";
        else if ( d.PaO2 >= 80 && d.PaO2 <= 100 )
            return "Normal";
        else 
            return "None";
      });

      PaO2Dim.dispose();
      PaO2CategoryDim.dispose();
      
      // pH 
      var pHDim = ndx.dimension(function(d) {
          return d.pH;
      });
  

      var pHCategoryDim = ndx.dimension(function(d) {
        if ( d.pH > 0 && d.pH < 7.35 )
            return "Low";
        else if ( d.pH > 7.45 && d.pH < 10 )
            return "High";
        else if ( d.pH >= 7.35 && d.pH <= 7.45 )
            return "Normal";
        else 
            return "None";
      });

      pHDim.dispose();
      pHCategoryDim.dispose();

      // Platelets 
      var PlateletsDim = ndx.dimension(function(d) {
          return d.Platelets;
      });
  

      var PlateletsCategoryDim = ndx.dimension(function(d) {
        if ( d.Platelets > 0 && d.Platelets < 150)
            return "Low";
        else if ( d.Platelets > 400)
            return "High";
        else if ( d.Platelets >= 150 && d.Platelets <= 400 )
            return "Normal";
        else 
            return "None";
      });

      PlateletsDim.dispose();
      PlateletsCategoryDim.dispose();

      // RespRate 
      var RespRateDim = ndx.dimension(function(d) {
          return d.RespRate;
      });
  

      var RespRateCategoryDim = ndx.dimension(function(d) {
        if ( d.RespRate > 0 && d.RespRate < 16)
            return "Low";
        else if ( d.RespRate> 20)
            return "High";
        else if ( d.RespRate >= 16 && d.RespRate<= 20)
            return "Healthy";
        else 
            return "None";
      });

      RespRateDim.dispose();
      RespRateCategoryDim.dispose();
      
      // SaO2 
      var SaO2Dim = ndx.dimension(function(d) {
          return d.SaO2;
      });
  

      var SaO2CategoryDim = ndx.dimension(function(d) {
        if ( d.SaO2 > 0 && d.SaO2 < 95 )
            return "Low";
        else if ( d.SaO2 > 100 )
            return "High";
        else if ( d.SaO2 >= 95 && d.SaO2 <= 100)
            return "Normal";
        else 
            return "None";
      });

      SaO2Dim.dispose();
      SaO2CategoryDim.dispose();

      // Temp 
      var TempDim = ndx.dimension(function(d) {
          return d.Temp;
      });
  

      var TempCategoryDim = ndx.dimension(function(d) {
        if ( d.Temp > 0 && d.Temp < 35.0 )
            return "Hypothermia";
        else if ( d.Temp >=35.0 && d.Temp <36.5 )
            return "Cold";
        else if ( d.Temp >= 36.5 && d.Temp <= 37.5 )
            return "Normal";
        else if ( d.Temp > 37.5 && d.Temp <= 40.0 )  
            return "Fever";
        else if ( d.Temp >= 40.0 )  
            return "Hyperpyrexia";
        else 
          return "None";
      });

      TempDim.dispose();
      TempCategoryDim.dispose();


      // Urine 
      var UrineDim = ndx.dimension(function(d) {
          return d.Urine;
      });
  

      var UrineCategoryDim = ndx.dimension(function(d) {
        if ( d.Urine > 0 && d.Urine < 400 )
            return "Low";
        else if ( d.Urine > 2000 )
            return "High";
        else if ( d.Urine >= 400  && d.Urine <= 2000 )
            return "Normal";
        else 
          return "None";
      });

      UrineDim.dispose();
      UrineCategoryDim.dispose();


      // WBC 
      var WBCDim = ndx.dimension(function(d) {
          return d.WBC;
      });
  

      var WBCCategoryDim = ndx.dimension(function(d) {
        if ( d.WBC > 0 && d.WBC < 4.0 )
            return "Low";
        else if ( d.WBC > 10.0 )
            return "High";
        else if ( d.WBC >= 4.0 && d.WBC <= 10.0 )
            return "Normal";
        else 
          return "None";
      });

      WBCDim.dispose();
      WBCCategoryDim.dispose();





      var ageGroup = ageDim.group(),
      genderGroup = genderDim.group(),
      icutypeGroup = icutypeDim.group(),
      survivalGroup = survivalDim.group(),
      deathGroup = deathDim.group(),

      bmiPieGroup = bmiPieDim.group(),
      bmiGroup = bmiDim.group(),

      bpPieGroup = bpPieDim.group(),
      bpGroup = bpDim.group();

      var albuminGroup = albuminDim.group();
      var albuminCategoryGroup = albuminCategoryDim.group();

      var alpGroup = alpDim.group();
      var alpCategoryGroup = alpCategoryDim.group();

      var altGroup = altDim.group();
      var altCategoryGroup = altCategoryDim.group();

      var astGroup = astDim.group();
      var astCategoryGroup = astCategoryDim.group();

      // Bilirubin
      var BilirubinGroup = BilirubinDim.group();
      var BilirubinCategoryGroup = BilirubinCategoryDim.group();
          /*
          .reduce(
          function (p,v) {
            ++p.count;
            p.total += v.first_bmi;
            p.avg = d3.round(p.total / p.count, 1);
            return p;
          },
          function (p,v) {
            --p.count;
            p.total -= v.first_bmi;
            p.avg = p.count ? d3.round( p.total / p.count, 1) : 0;
            return p;
          },
          // initialize p 
          function () {
            return{count: 0, total: 0, avg: 0};
          }
      );
      */

      
      // BUN
      var bunGroup = bunDim.group();
      var bunCategoryGroup = bunCategoryDim.group();
     
      // Cholesterol
      var CholesterolGroup = CholesterolDim.group();
      var CholesterolCategoryGroup = CholesterolCategoryDim.group();

      // Creatinine 
      var CreatinineGroup = CreatinineDim.group();
      var CreatinineCategoryGroup = CreatinineCategoryDim.group();

      // Glucose 
      var GlucoseGroup = GlucoseDim.group();
      var GlucoseCategoryGroup = GlucoseCategoryDim.group();

      // HCO3 
      var HCO3Group = HCO3Dim.group();
      var HCO3CategoryGroup = HCO3CategoryDim.group();

      // HCT 
      var HCTGroup = HCTDim.group();
      var HCTCategoryGroup = HCTCategoryDim.group();

      // K
      var KGroup = KDim.group();
      var KCategoryGroup = KCategoryDim.group();
      
      // Lactate
      var LactateGroup = LactateDim.group();
      var LactateCategoryGroup = LactateCategoryDim.group();

      // Serum magnesium 
      var MgGroup = MgDim.group();
      var MgCategoryGroup = MgCategoryDim.group();
      
      // Mechanical ventilation respiration 
      var MechVentCategoryGroup = MechVentCategoryDim.group();
      
      // Serum Sodium 
      var NaGroup = NaDim.group();
      var NaCategoryGroup = NaCategoryDim.group();

      // PaCO2 
      var PaCO2Group = PaCO2Dim.group();
      var PaCO2CategoryGroup = PaCO2CategoryDim.group();

      // PaO2 
      var PaO2Group = PaO2Dim.group();
      var PaO2CategoryGroup = PaO2CategoryDim.group();
      
      // pH 
      var pHGroup = pHDim.group();
      var pHCategoryGroup = pHCategoryDim.group();

      // Platelets 
      var PlateletsGroup = PlateletsDim.group();
      var PlateletsCategoryGroup = PlateletsCategoryDim.group();

      // RespRate 
      var RespRateGroup = RespRateDim.group();
      var RespRateCategoryGroup = RespRateCategoryDim.group();

      // SaO2Rate 
      var SaO2Group = SaO2Dim.group();
      var SaO2CategoryGroup = SaO2CategoryDim.group();
      
      // TempRate 
      var TempGroup = TempDim.group();
      var TempCategoryGroup = TempCategoryDim.group();

      // UrineRate 
      var UrineGroup = UrineDim.group();
      var UrineCategoryGroup = UrineCategoryDim.group();

      // WBC 
      var WBCGroup = WBCDim.group();
      var WBCCategoryGroup = WBCCategoryDim.group();


  ageRingChart
      .width(180).height(180)
      .dimension(ageDim)
      .group(ageGroup)
      .innerRadius(20);

  genderChart
      .width(180).height(180)
      .dimension(genderDim)
      .group(genderGroup)
      .innerRadius(0)
      /* (optional) by default pie chart will use group.key as its label
       * but you can overwrite it with a closure */
      .label(function (d) {
          if (genderChart.hasFilter() && !genderChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });


  icutypeChart
      .width(140).height(140)
      .dimension(icutypeDim)
      .group(icutypeGroup);

  survivalChart
      .width(140).height(140)
      .dimension(survivalDim)
      .group(survivalGroup);

  deathChart
      .width(140).height(140)
      .dimension(deathDim)
      .group(deathGroup);

  // Mechanical ventilation respiration
  MechVentCategoryChart
      .width(140).height(140)
      .dimension(MechVentCategoryDim)
      .group(MechVentCategoryGroup)
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (MechVentCategoryChart.hasFilter() && !MechVentCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });


  bmiChart
      .width(600).height(280)
      .renderArea(true)
      .dimension(bmiDim)
      .group(bmiGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(d3.scale.linear().domain([10,70]))
      //.elasticX(true)
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(13).gap(5))
      .brushOn(false)
      .xAxisLabel('BMI') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;
      // Add the base layer of the stack with group. The second parameter specifies a series name for use in the legend
      // The `.valueAccessor` will be used for the base layer
      //.valueAccessor(function (d) {
      //    return d.value.avg;
      //})
      // stack additional layers with `.stack`. The first paramenter is a new group.
      // The second parameter is the series name. The third is a value accessor.
      //.stack(monthlyMoveGroup, "Monthly Visits Total", function (d) {
      //    return d.value;
      //})
      // title can be called by any stack layer.
      //.title(function (d) {
      //    var value = d.value.avg ? d.value.avg : d.value;
      //    if (isNaN(value)) value = 0;
      //    return dateFormat(d.key) + "\n" + numberFormat(value);
      //});
  bmiPieChart
      .width(190).height(180)
      .dimension(bmiPieDim)
      .group(bmiPieGroup)
      .innerRadius(20)
      /* (optional) by default pie chart will use group.key as its label
       * but you can overwrite it with a closure */
      .label(function (d) {
          if (bmiPieChart.hasFilter() && !bmiPieChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });
      
  bpChart
      .width(600).height(280)
      .renderArea(true)
      .dimension(bpDim)
      .group(bpGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(d3.scale.linear().domain([10,180]))
      //.elasticX(true)
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(13).gap(5))
      .brushOn(false)
      .xAxisLabel('MAP') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;
  /*
  bpCategoryChart
      .width(190).height(180)
      .dimension(bpPieDim)
      .group(bpPieGroup)
      .innerRadius(20)
      .label(function (d) {
          if (bpCategoryChart.hasFilter() && !bpCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });
    */
  bpCategoryChart
      .width(600).height(280)
      .dimension(bpPieDim)
      .group(bpPieGroup)
      .colors(d3.scale.category10())
      .elasticX(true)
      ;

  albuminChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(albuminDim)
      .group(albuminGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(d3.scale.linear().domain([0.8,6.0]))
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(8))
      .brushOn(false)
      .xAxisLabel('Albumin') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  albuminCategoryChart
      .width(190).height(180)
      .dimension(albuminCategoryDim)
      .group(albuminCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      /* (optional) by default pie chart will use group.key as its label
       * but you can overwrite it with a closure */
      .label(function (d) {
          if (albuminCategoryChart.hasFilter() && !albuminCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });
      
  alpChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(alpDim)
      .group(alpGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(d3.scale.linear().domain([4,200]))
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(8))
      .brushOn(false)
      .xAxisLabel('ALP') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  alpCategoryChart
      .width(190).height(180)
      .dimension(alpCategoryDim)
      .group(alpCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      /* (optional) by default pie chart will use group.key as its label
       * but you can overwrite it with a closure */
      .label(function (d) {
          if (alpCategoryChart.hasFilter() && !alpCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });

  altChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(altDim)
      .group(altGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(d3.scale.linear().domain([1,100]))
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(8))
      .brushOn(false)
      .xAxisLabel('ALT') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  altCategoryChart
      .width(190).height(180)
      .dimension(altCategoryDim)
      .group(altCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      /* (optional) by default pie chart will use group.key as its label
       * but you can overwrite it with a closure */
      .label(function (d) {
          if (altCategoryChart.hasFilter() && !altCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });

  astChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(astDim)
      .group(astGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(d3.scale.linear().domain([1,100]))
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(8))
      .brushOn(false)
      .xAxisLabel('AST') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  astCategoryChart
      .width(190).height(180)
      .dimension(astCategoryDim)
      .group(astCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      /* (optional) by default pie chart will use group.key as its label
       * but you can overwrite it with a closure */
      .label(function (d) {
          if (astCategoryChart.hasFilter() && !astCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });

  // Bilirubin
  BilirubinChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(BilirubinDim)
      .group(BilirubinGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(d3.scale.linear().domain([0.1,3]))
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(8))
      .brushOn(false)
      .xAxisLabel('Bilirubin') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  BilirubinCategoryChart
      .width(190).height(180)
      .dimension(BilirubinCategoryDim)
      .group(BilirubinCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (BilirubinCategoryChart.hasFilter() && !BilirubinCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });
 
  // BUN
  bunChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(bunDim)
      .group(bunGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(d3.scale.linear().domain([1,40]))
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(8))
      .brushOn(false)
      .xAxisLabel('BUN') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  bunCategoryChart
      .width(190).height(180)
      .dimension(bunCategoryDim)
      .group(bunCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (bunCategoryChart.hasFilter() && !bunCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });
 
  //  Cholesterol 
  CholesterolChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(CholesterolDim)
      .group(CholesterolGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(d3.scale.linear().domain([20,330]))
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(8))
      .brushOn(false)
      .xAxisLabel('Cholesterol') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  CholesterolCategoryChart
      .width(190).height(180)
      .dimension(CholesterolCategoryDim)
      .group(CholesterolCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (CholesterolCategoryChart.hasFilter() && !CholesterolCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });

  // Creatinine 
  CreatinineChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(CreatinineDim)
      .group(CreatinineGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(d3.scale.linear().domain([0.2,4]))
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(8))
      .brushOn(false)
      .xAxisLabel('Creatinine') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  CreatinineCategoryChart
      .width(190).height(180)
      .dimension(CreatinineCategoryDim)
      .group(CreatinineCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (CreatinineCategoryChart.hasFilter() && !CreatinineCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });

  // Glucose 
  GlucoseChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(GlucoseDim)
      .group(GlucoseGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(d3.scale.linear().domain([10,300]))
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(8))
      .brushOn(false)
      .xAxisLabel('Glucose') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  GlucoseCategoryChart
      .width(190).height(180)
      .dimension(GlucoseCategoryDim)
      .group(GlucoseCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (GlucoseCategoryChart.hasFilter() && !GlucoseCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });

  // HCO3 Serum Bicarbonate 
  HCO3Chart
      .width(300).height(280)
      .renderArea(true)
      .dimension(HCO3Dim)
      .group(HCO3Group, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(d3.scale.linear().domain([7,38]))
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(8))
      .brushOn(false)
      .xAxisLabel('HCO3') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  HCO3CategoryChart
      .width(190).height(180)
      .dimension(HCO3CategoryDim)
      .group(HCO3CategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (HCO3CategoryChart.hasFilter() && !HCO3CategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });

  // HCT 
  HCTChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(HCTDim)
      .group(HCTGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(d3.scale.linear().domain([1,100]))
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(8))
      .brushOn(false)
      .xAxisLabel('HCT') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  HCTCategoryChart
      .width(190).height(180)
      .dimension(HCTCategoryDim)
      .group(HCTCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (HCTCategoryChart.hasFilter() && !HCTCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });

  // K
  KChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(KDim)
      .group(KGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(d3.scale.linear().domain([1.8,10]))
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(10))
      .brushOn(false)
      .xAxisLabel('K(Serum Potassium)') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  KCategoryChart
      .width(190).height(180)
      .dimension(KCategoryDim)
      .group(KCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (KCategoryChart.hasFilter() && !KCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });
  
  
  // Lactate 
  LactateChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(LactateDim)
      .group(LactateGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(d3.scale.linear().domain([0.4,10]))
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(10))
      .brushOn(false)
      .xAxisLabel('Lactate') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  LactateCategoryChart
      .width(190).height(180)
      .dimension(LactateCategoryDim)
      .group(LactateCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (LactateCategoryChart.hasFilter() && !LactateCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });

  // Serum magnesium 
  MgChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(MgDim)
      .group(MgGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(d3.scale.linear().domain([0.6,4]))
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(10))
      .brushOn(false)
      .xAxisLabel('Serum magnesium') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  MgCategoryChart
      .width(190).height(180)
      .dimension(MgCategoryDim)
      .group(MgCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (MgCategoryChart.hasFilter() && !MgCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });

  // Serum Sodium 
  NaChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(NaDim)
      .group(NaGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(d3.scale.linear().domain([125,155]))
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(10))
      .brushOn(false)
      .xAxisLabel('Serum Sodium') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  NaCategoryChart
      .width(190).height(180)
      .dimension(NaCategoryDim)
      .group(NaCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (NaCategoryChart.hasFilter() && !NaCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });


  // PaCO2 
  PaCO2Chart
      .width(300).height(280)
      .renderArea(true)
      .dimension(PaCO2Dim)
      .group(PaCO2Group, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(
        d3.scale.linear().domain([
                    20,70])
                    .range([
                        "red","green"
                    ])
        )
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(10))
      .brushOn(false)
      .xAxisLabel('PaCO2') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  PaCO2CategoryChart
      .width(190).height(180)
      .dimension(PaCO2CategoryDim)
      .group(PaCO2CategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (PaCO2CategoryChart.hasFilter() && !PaCO2CategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });

  // PaO2 
  PaO2Chart
      .width(300).height(280)
      .renderArea(true)
      .dimension(PaO2Dim)
      .group(PaO2Group, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(
        d3.scale.linear().domain([
                    20,500])
                    .range([
                        "red","green"
                    ])
        )
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(10))
      .brushOn(false)
      .xAxisLabel('PaO2') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  PaO2CategoryChart
      .width(190).height(180)
      .dimension(PaO2CategoryDim)
      .group(PaO2CategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (PaO2CategoryChart.hasFilter() && !PaO2CategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });

  // pH 
  pHChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(pHDim)
      .group(pHGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(
        d3.scale.linear().domain([
                    7.0,7.63])
                    .range([
                        "red","green"
                    ])
        )
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(10))
      .brushOn(false)
      .xAxisLabel('pH') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  pHCategoryChart
      .width(190).height(180)
      .dimension(pHCategoryDim)
      .group(pHCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (pHCategoryChart.hasFilter() && !pHCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });

  // Platelets 
  PlateletsChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(PlateletsDim)
      .group(PlateletsGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(
        d3.scale.linear().domain([
                    8,1000])
                    .range([
                        "red","green"
                    ])
        )
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(10))
      .brushOn(false)
      .xAxisLabel('Platelets') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  PlateletsCategoryChart
      .width(190).height(180)
      .dimension(PlateletsCategoryDim)
      .group(PlateletsCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (PlateletsCategoryChart.hasFilter() && !PlateletsCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });


  // RespRate 
  RespRateChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(RespRateDim)
      .group(RespRateGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(
        d3.scale.linear().domain([
                    7,45])
                    .range([
                        "red","green"
                    ])
        )
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(10))
      .brushOn(false)
      .xAxisLabel('RespRate') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  RespRateCategoryChart
      .width(190).height(180)
      .dimension(RespRateCategoryDim)
      .group(RespRateCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (RespRateCategoryChart.hasFilter() && !RespRateCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });


  // SaO2 
  SaO2Chart
      .width(300).height(280)
      .renderArea(true)
      .dimension(SaO2Dim)
      .group(SaO2Group, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(
        d3.scale.linear().domain([
                    90,100])
                    .range([
                        "red","green"
                    ])
        )
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(10))
      .brushOn(false)
      .xAxisLabel('SaO2') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  SaO2CategoryChart
      .width(190).height(180)
      .dimension(SaO2CategoryDim)
      .group(SaO2CategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (SaO2CategoryChart.hasFilter() && !SaO2CategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });



  // Temp 
  TempChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(TempDim)
      .group(TempGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(
        d3.scale.linear().domain([
                    30,41])
                    .range([
                        "red","green"
                    ])
        )
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(10))
      .brushOn(false)
      .xAxisLabel('Temp') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  TempCategoryChart
      .width(190).height(180)
      .dimension(TempCategoryDim)
      .group(TempCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (TempCategoryChart.hasFilter() && !TempCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });



  // Urine 
  UrineChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(UrineDim)
      .group(UrineGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(
        d3.scale.linear().domain([
                    5,2500])
                    .range([
                        "red","green"
                    ])
        )
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(10))
      .brushOn(false)
      .xAxisLabel('Urine Output') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  UrineCategoryChart
      .width(190).height(180)
      .dimension(UrineCategoryDim)
      .group(UrineCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (UrineCategoryChart.hasFilter() && !UrineCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });



  // WBC 
  WBCChart
      .width(300).height(280)
      .renderArea(true)
      .dimension(WBCDim)
      .group(WBCGroup, "Patient")
    
      //.transitionDuration(1000)
      .margins({top: 10, right: 10, bottom: 35, left: 40})
      .mouseZoomable(true)
      // Specify a range chart to link the brush extent of the range with the zoom focue of the current chart.
      .x(
        d3.scale.linear().domain([
                    0.1,34])
                    .range([
                        "red","green"
                    ])
        )
      .elasticY(true)
      //.renderVerticalGridLines(true)
      .renderHorizontalGridLines(true)
      .legend(dw.legend().x(800).y(10).itemHeight(8).gap(10))
      .brushOn(false)
      .xAxisLabel('WBC') // (optional) render an axis label below the x axis
      .yAxisLabel('Patients Number') // (optional) render a vertical axis lable left of the y axis
      ;

  WBCCategoryChart
      .width(190).height(180)
      .dimension(WBCCategoryDim)
      .group(WBCCategoryGroup)
      .innerRadius(20)
      .colors(d3.scale.category20())
      //(optional) by default pie chart will use group.key as its label
      //  but you can overwrite it with a closure 
      .label(function (d) {
          if (WBCCategoryChart.hasFilter() && !WBCCategoryChart.hasFilter(d.key))
              return d.key + "(0%)";
          var label = d.key;
          if(all.value())
              label += "(" + Math.floor(d.value / all.value() * 100) + "%)";
          return label;
      });








  dw.renderAll();
});

//#### Version
//Determine the current version of dw with `dw.version`
d3.selectAll("#version").text(dw.version);
