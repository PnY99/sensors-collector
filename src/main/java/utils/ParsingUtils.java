package utils;

public class ParsingUtils {
    public static float parseFloat(String value)
    {
        StringBuilder res = new StringBuilder();
        char c;
        //conserva solo numeri e virgola
        for(int i=0; i<value.length(); i++)
        {
            c=value.charAt(i);
            if(Character.isDigit(c) || c=='.' || c == '-')
            {
                res.append(c);
            }
        }
        return Float.parseFloat(res.toString());
    }

    public static String replaceDuplicateSpaces(String s)
    {
        StringBuilder res = new StringBuilder();
        char lastChar = 'a';
        char now;
        for(int i=0; i<s.length(); i++)
        {
            now = s.charAt(i);
            if(lastChar==' ' && now == ' ') //ho due spazi consecutivi, non copiare lo spazio nel risultato
            {
                continue;
            }
            lastChar=now;
            res.append(now);
        }
        return res.toString();
    }
}
