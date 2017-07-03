package de.tud.fusion;

import org.jetbrains.annotations.NotNull;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

/**
 *
 */
public class RConnectionHandler {

    private RConnection rConnection;

    /**
     * constructor
     * @param host R host
     * @param workspace R workspace
     * @param startRserve flag: try to start Rserve, if not yet available
     * @throws REXPMismatchException
     * @throws REngineException
     */
    public RConnectionHandler(@NotNull String host, @NotNull String workspace, boolean startRserve) throws REXPMismatchException, REngineException {
        rConnection = getRserveConnection(host, startRserve);
        //set workspace
        REXP xp = rConnection.parseAndEval("try(setwd('"+ workspace + "'))");
        if (xp.inherits("try-error"))
            throw new RserveException(rConnection, "failed to load R workspace: " + xp.asString());
    }

    /**
     * default constructor
     * @throws REXPMismatchException
     * @throws REngineException
     */
    public RConnectionHandler() throws REXPMismatchException, REngineException {
        this("localhost", System.getProperty("java.io.tmpdir").replace("\\", "/"), true);
    }

    /**
     * get Rserve connection and start Rserve, if startRserve == TRUE
     * @param host Host server, null = localhost
     * @param startRserve flag: try to start Rserve via CMD
     * @return R connection object
     * @throws RserveException
     */
    private @NotNull RConnection getRserveConnection(@NotNull String host, boolean startRserve) throws RserveException {
        try {
            return new RConnection(host);
        } catch(RserveException e) {
            if (e.getMessage().startsWith("Cannot connect") && startRserve){
                //try to start local Rserve if connection was refused (Linux)
                if (System.getProperty("os.name").toLowerCase().contains("linux")) {
                    String rserveStartCMD = "R CMD Rserve --vanilla";
                    try {
                        int exitValue = Runtime.getRuntime().exec(rserveStartCMD).waitFor();
                        if (exitValue == 0) return new RConnection(host);
                        else throw e;
                    } catch(Exception ie){
                        System.err.println("failed to start Rserve from Linux");
                        throw e;
                    }
                }
                //try to start local Rserve if connection was refused (Windows)
                if (System.getProperty("os.name").toLowerCase().contains("windows")) {
                    String rserveStartCMD = "Rscript -e \"library(Rserve); Rserve() --vanilla --slave\"";
                    try {
                        Runtime.getRuntime().exec(rserveStartCMD);
                        Runtime.getRuntime().runFinalization();
                        return new RConnection(host);
                    } catch(Exception ie){
                        System.err.println("failed to start Rserve from Windows");
                        throw e;
                    }
                }
            }
            else throw e;
        }
        //never reached
        throw new RuntimeException("Cannot establish Rserve connection");
    }

    /**
     * Load RData workspace
     * @param rData Name of the RData file, relative to current R workspace
     * @throws REXPMismatchException
     * @throws REngineException
     */
    public void loadRData(@NotNull String rData) throws REXPMismatchException, REngineException {
        REXP xp = rConnection.parseAndEval("try(load('" + rData + "'))");
        if (xp.inherits("try-error"))
            throw new RserveException(rConnection, "failed to load RData workspace");
    }

    /**
     * Load required R libraries
     * @param libraries Array of libraries to be loaded
     * @throws REXPMismatchException
     * @throws REngineException
     */
    public void loadLibraries(@NotNull String[] libraries) throws REXPMismatchException, REngineException {
        for(String library : libraries){
            if (rConnection.parseAndEval("suppressWarnings(require('" + library + "',quietly=TRUE))").asInteger() == 0)
                throw new RserveException(rConnection, "failed to load library '" + library + "'");
        }
    }

    /**
     * Executes an R function
     * @param function name of the R function
     * @param parameters array of parameters required by the function
     * @return expression returned from function
     * @throws REngineException
     * @throws REXPMismatchException
     */
    private @NotNull REXP executeRFunction(@NotNull String function, @NotNull String[] parameters) throws REngineException, REXPMismatchException {
        //create request
        String request = "try(" + function + "(";
        for(String parameter : parameters){
            request += parameter + "," ;
        }
        //remove last ","
        request = request.substring(0, request.length() - 1);
        request += "))";
        //execute function
        REXP xp = rConnection.parseAndEval(request);
        if (xp.inherits("try-error")) {
            throw new RserveException(rConnection, "failed to execute function '" + function + "'; \nrequest: " + request + "; \nError: " + xp.asString());
        }
        return xp;
    }

    /**
     * Executes an R function (return: void)
     * @param function name of the R function
     * @param parameters array of parameters required by the function
     * @throws REngineException
     * @throws REXPMismatchException
     */
    public void executeVoidFunction(@NotNull String function, @NotNull String[] parameters) throws REngineException, REXPMismatchException {
        executeRFunction(function, parameters);
    }

    /**
     * Executes an R function (return: String)
     * @param function name of the R function
     * @param parameters array of parameters required by the function
     * @throws REngineException
     * @throws REXPMismatchException
     */
    public @NotNull String executeStringFunction(@NotNull String function, @NotNull String[] parameters) throws REngineException, REXPMismatchException {
        return executeRFunction(function, parameters).asString();
    }

    /**
     * Executes an R function (return: Bytes[])
     * @param function name of the R function
     * @param parameters array of parameters required by the function
     * @throws REngineException
     * @throws REXPMismatchException
     */
    public byte[] executeByteFunction(@NotNull String function, @NotNull String[] parameters) throws REngineException, REXPMismatchException {
        return executeRFunction(function, parameters).asBytes();
    }

    /**
     * close r connection
     */
    public void closeConnection() {
        rConnection.close();
    }

    /**
     * shutdown Rserve
     * @throws RserveException
     */
    public void shutdown() throws RserveException {
        rConnection.shutdown();
    }

}
