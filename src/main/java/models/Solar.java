package models;

import exceptions.PropertyNotFoundException;
import utils.Configuration;
import utils.DateUtils;
import utils.ParsingUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;

public class Solar {
    private float energy = 0, iac = 0, vac = 0, idc = 0, vdc = 0, power = 0;
    private boolean available;
    private LocalDateTime date = LocalDateTime.now();
    private int inverterID;

    public float getEnergy() {
        return energy;
    }

    public void setEnergy(float energy) {
        this.energy = energy;
    }

    public float getIac() {
        return iac;
    }

    public void setIac(float iac) {
        this.iac = iac;
    }

    public float getVac() {
        return vac;
    }

    public void setVac(float vac) {
        this.vac = vac;
    }

    public float getIdc() {
        return idc;
    }

    public void setIdc(float idc) {
        this.idc = idc;
    }

    public float getVdc() {
        return vdc;
    }

    public void setVdc(float vdc) {
        this.vdc = vdc;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public int getInverterID() {
        return inverterID;
    }

    public void setInverterID(int inverterID) {
        this.inverterID = inverterID;
    }

    public static Solar read(int inverterID) throws IOException, PropertyNotFoundException {
        boolean streamAvailable = false;
        Solar s = new Solar();

        s.setInverterID(inverterID);

        String line;
        String[] fields;
        Process cmd;
        if(Configuration.isDebug()) {
            cmd = Runtime.getRuntime().exec("sshpass -p "+Configuration.getString("debug.remote.password")+" ssh pi@"+Configuration.getString("debug.remote.address")+" "+Configuration.getString("sbf.path")+" -v2 -nosql -nocsv -finq");
        } else {
            cmd = Runtime.getRuntime().exec(Configuration.getString("sbf.path") + " -v2 -nosql -nocsv -finq");
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(cmd.getInputStream()));
        //parsing
        while((line=reader.readLine())!=null)
        {
            streamAvailable = true;
            //inverter spento, o errore connessione
            if(line.contains("CRITICAL"))
            {
                s.setAvailable(false);
                return s;
            }

            line = ParsingUtils.replaceDuplicateSpaces(line);
            fields=line.split("[\\s\n\t]");

            for(int i=0; i<fields.length; i++)
            {
                //cerco i vari campi: l'elemento successivo è il valore cercato
                if(fields[i].equals("Uac:") && s.getVac()==0)
                {
                    s.setVac(ParsingUtils.parseFloat(fields[i+1]));
                }
                if(fields[i].equals("Iac:") && s.getIac()==0)
                {
                    s.setIac(ParsingUtils.parseFloat(fields[i+1]));
                }
                if(fields[i].equals("Udc:") && s.getVdc()==0)
                {
                    s.setVdc(ParsingUtils.parseFloat(fields[i+1]));
                }
                if(fields[i].equals("Idc:") && s.getIdc()==0)
                {
                    s.setIdc(ParsingUtils.parseFloat(fields[i+1]));
                }
                if(fields[i].equals("EToday:") && s.getEnergy()==0)
                {
                    s.setEnergy(ParsingUtils.parseFloat(fields[i+1]));
                }
                if(fields[i].equals("Pac:") && s.getPower()==0)
                {
                    s.setPower(ParsingUtils.parseFloat(fields[i+1]));
                }
            }
        }
        if(streamAvailable) {
            s.setAvailable(true);
        }
        reader.close();
        return s;
    }

    @Override
    public String toString()
    {
        return DateUtils.localDateTimeToTimestamp(this.getDate()) + " - Solar: " + "Vac:"+this.vac+" Iac:"+this.iac+" Vdc:"+this.vdc+" Idc:"+this.idc+" Power:"+this.power+" Energy:"+this.energy;
    }
}
