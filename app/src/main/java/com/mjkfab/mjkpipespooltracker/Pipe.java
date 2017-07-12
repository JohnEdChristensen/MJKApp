package com.mjkfab.mjkpipespooltracker;
class Pipe {
    private String ID, branch_plant,spoolNo,orderNo,weight,area,paintSystem,inboundLoadNum,
            internalBlast,cleaningMethod,dateReceived, surfacePreparation,OSNDDetail,loadListNum,trailerNum,
            invoiced,location,dateUnloaded,shiftUnloaded,userUnloaded,dateBlasted,shiftBlasted,userBlasted,datePainted,shiftPainted,
            userPainted,dateTouchedUp,shiftTouchedUp,userTouchedUp,dateLoaded,shiftLoaded,userLoaded;
    private String[] parameterList;
    private static String[] parameterLabel = {"ID","Branch/Plant","SpoolNo.","OrderNo.","Weight","Area",
            "Paint System","Inbound Load #","Internal Blast","Cleaning Method","Date Received",
            "Surface Preparation", "OS&D Detail","Load List #","Trailer #", "Invoiced","Location","Date Unloaded",
            "Shift Unloaded","User Unloaded","Date Blasted", "Shift Blasted","User Blasted", "Date Painted", "Shift Painted",
            "User Painted","Date Touched Up","Shift Touched Up","User Touched Up", "Date Loaded", "Shift Loaded","User Loaded"};




    Pipe(String pipeLine){
        pipeLine+=",END";
        parameterList = pipeLine.split(",");
        ID = parameterList[0];
        branch_plant = parameterList[1];
        spoolNo = parameterList[2];
        orderNo = parameterList[3];
        weight = parameterList[4];
        area = parameterList[5];
        paintSystem = parameterList[6];
        inboundLoadNum = parameterList[7];
        internalBlast = parameterList[8];
        cleaningMethod = parameterList[9];
        dateReceived = parameterList[10];
        surfacePreparation = parameterList[11];
        OSNDDetail = parameterList[12];
        loadListNum = parameterList[13];
        trailerNum = parameterList[14];
        invoiced = parameterList[15];
        location = parameterList[16];
        dateUnloaded = parameterList[17];
        shiftUnloaded = parameterList[18];
        userUnloaded = parameterList[19];
        dateBlasted = parameterList[20];
        shiftBlasted = parameterList[21];
        userBlasted = parameterList[22];
        datePainted = parameterList[23];
        shiftPainted = parameterList[24];
        userPainted = parameterList[25];
        dateTouchedUp = parameterList[26];
        shiftTouchedUp = parameterList[27];
        userTouchedUp = parameterList[28];
        dateLoaded = parameterList[29];
        shiftLoaded = parameterList[30];
        userLoaded = parameterList[31];
        for(int i =0; i < parameterList.length; i++){
            if(parameterList[i].equals("null")){
                parameterList[i] = "";
            }
        }
    }
    public String pipeToString() {
        String pipeLine;
        pipeLine = ID + "," + branch_plant + "," + spoolNo + "," + orderNo + "," + weight + "," + area + "," +
                paintSystem + "," + inboundLoadNum + "," + internalBlast + "," + cleaningMethod + "," +
                dateReceived + "," + surfacePreparation + "," + OSNDDetail + "," + loadListNum + "," +
                trailerNum + "," + invoiced + "," + location + "," + dateUnloaded + "," + shiftUnloaded + "," + userUnloaded + "," +
                dateBlasted + "," + shiftBlasted + "," + userBlasted + "," + datePainted + "," + shiftPainted + "," +
                userPainted + "," + dateTouchedUp + "," + shiftTouchedUp + "," + userTouchedUp + "," +
                dateLoaded + "," + shiftLoaded + "," + userLoaded;
        return pipeLine;
    }

    public String getID() {
        return ID;
    }
    public static String[] getParameterLabel() {
        return parameterLabel;
    }
    public String[] getParameterData(){
        return parameterList;
    }

}

