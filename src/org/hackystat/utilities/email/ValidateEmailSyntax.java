package org.hackystat.utilities.email;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

/**
 * Validate syntax of email addresses. Does not probe to see if mailserver exists in DNS or online.
 * See MailProber for that. See ValidateEmailFile for an example of how to use this class.
 * 
 * Minor reformatting.  Found in:
 * http://www.velocityreviews.com/forums/
 *  t128486-re-can-javamail-detect-a-nonexistant-email-address.html
 * 
 * @author Roedy Green, Canadian Mind Products
 * @author Philip Johnson. 
 * @version 1.0 to do: check validity of & in first part of email address. Appears in practice.
 */
public final class ValidateEmailSyntax {
  
  /** Make this class noninstantiable. */
  private ValidateEmailSyntax() {
    // Do nothing.
  }
  
  /**
   * Returns true if the email appears to be valid.  
   * @param email The email address of interest. 
   * @return True if it appears to be valid. 
   */
  public static boolean isValid(String email) {
    return howValid(email) >= 2;
  }

  /**
   * Check how likely an email address is to be valid. The higher the number returned, the more
   * likely the address is valid. This method does not probe the internet in any way to see if the
   * corresponding mail server or domain exists.
   * 
   * @param email bare computer email address. e.g. No "Roedy Green" <> style addresses. No local
   *        addresses, eg. roedy.
   * 
   * @return 0 = email address is definitely malformed, e.g. missing @. ends in .invalid <br>
   *         1 = address does not meet one of the valid patterns below. It still might be ok
   *         according to some obscure rule in RFC 822 Java InternetAddress accepts it as valid.
   *         <br>
   *         2 = unknown top level domain. <br>
   *         3 = dots at beginning or end, doubled in name. <br>
   *         4 = address of form xxx@[209.139.205.2] using IP <br>
   *         5 = address of form Dots _ or - in first part of name <br>
   *         6 = addreess of form rare, but known, domain <br>
   *         7 = address of form or any national suffix. <br>
   *         8 = address of form the matching this national suffix, e.g. .ca in Canada, .de in
   *         Germany <br>
   *         9 = address of form .org .net .edu .gov ..biz -- official domains
   */
  private static int howValid(String email) { //NOPMD (reassign email)
    if (email == null) {
      return 0;
    }
    email = email.trim().toLowerCase();
    int dotPlace = email.lastIndexOf('.');
    if (0 < dotPlace && dotPlace < email.length() - 1) {
      /* have at least x.y */
      String tld = email.substring(dotPlace + 1);
      if (badTLDs.contains(tld)) {
        /* deliberate invalid address */
        return 0;
      }
      // make sure none of fragments start or end in _ or -
      String[] fragments = splitter.split(email);
      boolean clean = true;
      for (int i = 0; i < fragments.length; i++) {
        if (fragments[i].startsWith("_") || fragments[i].endsWith("_")
            || fragments[i].startsWith("-") || fragments[i].endsWith("-")) {
          clean = false;
          break;
        }
      } // end for
      if (clean) {
        Matcher m9 = p9.matcher(email);
        if (m9.matches()) {
          if (officialTLDs.contains(tld)) {
            return 9;
          }
          else if (thisCountry.equals(tld)) {
            return 8;
          }
          else if (nationalTLDs.contains(tld)) {
            return 7;
          }
          else if (rareTLDs.contains(tld)) { //NOPMD
            return 6;
          }
          else {
            return 3; /* unknown tld */
          }
        }
        // allow dots in name
        Matcher m5 = p5.matcher(email);
        if (m5.matches()) {
          if (officialTLDs.contains(tld)) {
            return 5;
          }
          else if (thisCountry.equals(tld)) {
            return 5;
          }
          else if (nationalTLDs.contains(tld)) {
            return 5;
          }
          else if (rareTLDs.contains(tld)) {
            return 5;
          }
          else {
            return 2; /* unknown tld */
          }
        }

        // IP
        Matcher m4 = p4.matcher(email);
        if (m4.matches()) {
          return 4; /* can't tell TLD */ 
        }

        // allow even lead/trail dots in name, except at start of domain
        Matcher m3 = p3.matcher(email);
        if (m3.matches()) {
          if (officialTLDs.contains(tld)) {
            return 3;
          }
          else if (thisCountry.equals(tld)) {
            return 3;
          }
          else if (nationalTLDs.contains(tld)) {
            return 3;
          }
          else if (rareTLDs.contains(tld)) {
            return 3;
          }
          else {
            return 2; /* unknown domain */ 
          }
        }
      } // end if clean
    }
    // allow even unclean addresses, and addresses without a TLD to have a whack at passing RFC:822
    try {

      /*
       * see if InternetAddress likes it, it follows RFC:822. It will names without domains though.
       */
      InternetAddress.parse(email, true /* strict */);
      // it liked it, no exception happened. Seems very sloppy.
      return 1;
    }
    catch (AddressException e) {
      // it did not like it
      return 0;
    }
  }
  
 

  // allow _ - in name, lead and trailing ones are filtered later, no +.
  private static Pattern p9 = Pattern.compile("[a-z0-9\\-_]++@[a-z0-9\\-_]++(\\.[a-z0-9\\-_]++)++");

  // to split into fields
  private static Pattern splitter = Pattern.compile("[@\\.]");

  // to allow - _ dots in name, no +
  private static Pattern p5 = Pattern
      .compile("[a-z0-9\\-_]++(\\.[a-z0-9\\-_]++)*@[a-z0-9\\-_]++(\\.[a-z0-9\\-_]++)++");

  // IP style names, no +
  private static Pattern p4 = Pattern
      .compile("[a-z0-9\\-_]++(\\.[a-z0-9\\-_]++)*@\\[([0-9]{1,3}\\.){3}[0-9]{1,3}\\]");

  // allow dots anywhere, but not at start of domain name, no +
  private static Pattern p3 = Pattern
      .compile("[a-z0-9\\-_\\.]++@[a-z0-9\\-_]++(\\.[a-z0-9\\-_]++)++");

  /**
   * build a HashSet from a array of String literals.
   * 
   * @param list array of strings
   * @return HashSet you can use to test if a string is in the set.
   */
  private static Set<String> hmaker(String[] list) {
    Set<String> map = new HashSet<String>(Math.max((int) (list.length / .75f) + 1, 16));
    for (int i = 0; i < list.length; i++) {
      map.add(list[i]);
    }
    return map;
  }

  private static final String thisCountry = Locale.getDefault().getCountry().toLowerCase();

  private static final Set<String> officialTLDs = hmaker(new String[] { "aero", "biz", "coop",
      "com", "edu", "gov", "info", "mil", "museum", "name", "net", "org", "pro", });

  private static final Set<String> rareTLDs = hmaker(new String[] { "cam", "mp3", "agent",
      "art", "arts", "asia", "auction", "aus", "bank", "cam", "chat", "church", "club", "corp",
      "dds", "design", "dns2go", "e", "email", "exp", "fam", "family", "faq", "fed", "film",
      "firm", "free", "fun", "g", "game", "games", "gay", "ger", "globe", "gmbh", "golf", "gov",
      "help", "hola", "i", "inc", "int", "jpn", "k12", "kids", "law", "learn", "llb", "llc", "llp",
      "lnx", "love", "ltd", "mag", "mail", "med", "media", "mp3", "netz", "nic", "nom", "npo",
      "per", "pol", "prices", "radio", "rsc", "school", "scifi", "sea", "service", "sex", "shop",
      "sky", "soc", "space", "sport", "tech", "tour", "travel", "usvi", "video", "web", "wine",
      "wir", "wired", "zine", "zoo", });

  private static final Set<String> nationalTLDs = hmaker(new String[] { "ac", "ad", "ae", "af",
      "ag", "ai", "al", "am", "an", "ao", "aq", "ar", "as", "at", "au", "aw", "az", "ba", "bb",
      "bd", "be", "bf", "bg", "bh", "bi", "bj", "bm", "bn", "bo", "br", "bs", "bt", "bv", "bw",
      "by", "bz", "ca", "cc", "cd", "cf", "cg", "ch", "ci", "ck", "cl", "cm", "cn", "co", "cr",
      "cu", "cv", "cx", "cy", "cz", "de", "dj", "dk", "dm", "do", "dz", "ec", "ee", "eg", "eh",
      "er", "es", "et", "fi", "fj", "fk", "fm", "fo", "fr", "fx", "ga", "gb", "gd", "ge", "gf",
      "gg", "gh", "gi", "gl", "gm", "gn", "gp", "gq", "gr", "gs", "gt", "gu", "gw", "gy", "hk",
      "hm", "hn", "hr", "ht", "hu", "id", "ie", "il", "im", "in", "io", "iq", "ir", "is", "it",
      "je", "jm", "jo", "jp", "ke", "kg", "kh", "ki", "km", "kn", "kp", "kr", "kw", "ky", "kz",
      "la", "lb", "lc", "li", "lk", "lr", "ls", "lt", "lu", "lv", "ly", "ma", "mc", "md", "mg",
      "mh", "mk", "ml", "mm", "mn", "mo", "mp", "mq", "mr", "ms", "mt", "mu", "mv", "mw", "mx",
      "my", "mz", "na", "nc", "ne", "nf", "ng", "ni", "nl", "no", "np", "nr", "nu", "nz", "om",
      "pa", "pe", "pf", "pg", "ph", "pk", "pl", "pm", "pn", "pr", "ps", "pt", "pw", "py", "qa",
      "re", "ro", "ru", "rw", "sa", "sb", "sc", "sd", "se", "sg", "sh", "si", "sj", "sk", "sl",
      "sm", "sn", "so", "sr", "st", "sv", "sy", "sz", "tc", "td", "tf", "tg", "th", "tj", "tk",
      "tm", "tn", "to", "tp", "tr", "tt", "tv", "tw", "tz", "ua", "ug", "uk", "um", "us", "uy",
      "uz", "va", "vc", "ve", "vg", "vi", "vn", "vu", "wf", "ws", "ye", "yt", "yu", "za", "zm",
      "zw", });

  private static final Set<String> badTLDs = hmaker(new String[] { "invalid", "nowhere", 
      "noone", });
}
