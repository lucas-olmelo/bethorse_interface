package com.bethorse.ig_bethorse;

import java.util.InputMismatchException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validacoes {
    
    public boolean isCPF(String CPF) {
        // considera-se erro CPF's formados por uma sequencia de numeros iguais
        if (CPF.equals("00000000000") ||
            CPF.equals("11111111111") ||
            CPF.equals("22222222222") || CPF.equals("33333333333") ||
            CPF.equals("44444444444") || CPF.equals("55555555555") ||
            CPF.equals("66666666666") || CPF.equals("77777777777") ||
            CPF.equals("88888888888") || CPF.equals("99999999999") ||
            (CPF.length() != 11))
            return(false);

        char dig10, dig11;
        int sm, i, r, num, peso;

        // "try" - protege o codigo para eventuais erros de conversao de tipo (int)
        try {
            // Calculo do 1o. Digito Verificador
            sm = 0;
            peso = 10;
            for (i=0; i<9; i++) {
            // converte o i-esimo caractere do CPF em um numero:
            // por exemplo, transforma o caractere '0' no inteiro 0
            // (48 eh a posicao de '0' na tabela ASCII)
            num = (int)(CPF.charAt(i) - 48);
            sm = sm + (num * peso);
            peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                dig10 = '0';
            else dig10 = (char)(r + 48); // converte no respectivo caractere numerico

            // Calculo do 2o. Digito Verificador
            sm = 0;
            peso = 11;
            for(i=0; i<10; i++) {
            num = (int)(CPF.charAt(i) - 48);
            sm = sm + (num * peso);
            peso = peso - 1;
            }

            r = 11 - (sm % 11);
            if ((r == 10) || (r == 11))
                 dig11 = '0';
            else dig11 = (char)(r + 48);

            // Verifica se os digitos calculados conferem com os digitos informados.
            if ((dig10 == CPF.charAt(9)) && (dig11 == CPF.charAt(10)))
                 return(true);
            else return(false);
        } catch (InputMismatchException erro) {
            return(false);
        }
    }
    
    public boolean isValidEmailAddressRegex(String email) {
        boolean isEmailIdValid = false;
        if (email != null && email.length() > 0) {
            String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(email);
            if (matcher.matches()) {
                isEmailIdValid = true;
            }
        }
        return isEmailIdValid;
    }
    
    public boolean isValidPassword(String password){
  
        // Regex to check valid password.
        String regex = "^(?=.*[0-9])"+ "(?=.*[a-z])(?=.*[A-Z])"+ "(?=.*[@#$%^&+=-_])"+ "(?=\\S+$).{8,20}$";
  
        // Compile the ReGex
        Pattern p = Pattern.compile(regex);
  
        // If the password is empty
        // return false
        if (password == null) {
            return false;
        }
  
        // Pattern class contains matcher() method
        // to find matching between given password
        // and regular expression.
        Matcher m = p.matcher(password);
  
        // Return if the password
        // matched the ReGex
        return m.matches();
    }
    
    public boolean validarNome(String nome){
         if((nome != null) && (nome.isEmpty() == false) && (nome.indexOf(" ") >= 1)){
             return true;
         }else{
             return false;
         }     
     }
    
    // To check whether number is valid or not
    public boolean validarTelefone(String telefone) {
         
        //retira todos os caracteres não-numéricos (incluindo espaço,tab, etc)
        telefone = telefone.replaceAll("\\D","");

        //verifica se tem a qtde de numeros correta
        if (!(telefone.length() >= 10 && telefone.length() <= 11)) return false;

        //Se tiver 11 caracteres, verificar se começa com 9 o celular
        if (telefone.length() == 11 && Integer.parseInt(telefone.substring(2, 3)) != 9) return false;

        //verifica se o numero foi digitado com todos os dígitos iguais
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(telefone.charAt(0)+"{"+telefone.length()+"}");
        java.util.regex.Matcher m = p.matcher(telefone);
        if(m.find()) return false;

        //DDDs validos
        Integer[] codigosDDD = {
            11, 12, 13, 14, 15, 16, 17, 18, 19,
            21, 22, 24, 27, 28, 31, 32, 33, 34,
            35, 37, 38, 41, 42, 43, 44, 45, 46,
            47, 48, 49, 51, 53, 54, 55, 61, 62,
            64, 63, 65, 66, 67, 68, 69, 71, 73,
            74, 75, 77, 79, 81, 82, 83, 84, 85,
            86, 87, 88, 89, 91, 92, 93, 94, 95,
            96, 97, 98, 99};
        //verifica se o DDD é valido (sim, da pra verificar rsrsrs)
        if ( java.util.Arrays.asList(codigosDDD).indexOf(Integer.parseInt(telefone.substring(0, 2))) == -1) return false;

        //Se o número só tiver dez digitos não é um celular e por isso o número logo após o DDD deve ser 2, 3, 4, 5 ou 7 
        Integer[] prefixos = {2, 3, 4, 5, 7};

        if (telefone.length() == 10 && java.util.Arrays.asList(prefixos).indexOf(Integer.parseInt(telefone.substring(2, 3))) == -1) return false;

        //se passar por todas as validações acima, então está tudo certo
        return true;
    }
}
