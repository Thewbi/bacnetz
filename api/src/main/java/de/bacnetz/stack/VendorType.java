package de.bacnetz.stack;

public enum VendorType {

    // @formatter:off

//    0;ASHRAE;Stephanie Reiniche, MOS;1791 Tullie Circle, N.E. Atlanta, GA 30329 USA
//    1;NIST;Steven T. Bushby;Mechanical Systems and Controls Building 226, Room B114 Gaithersburg, MD 20899 USA
//    2;The Trane Company;Daniel Kollodge;4833 White Bear Parkway St. Paul, MN 55110 USA
//    3;McQuay International;Dan Halvorson, Controls Product Manager;13600 Industrial Park Blvd. Minneapolis, MN 55441 USA
//    4;PolarSoft;David M. Fisher;914 South Aiken Avenue Pittsburgh, PA 15232-2212 USA
//    5;Johnson Controls, Inc.;John Ruiz;507 East Michigan Street Milwaukee, WI 53201 USA
//    6;American Auto-Matrix;Will Kay, Operations Director;One Technology Dr. Export, PA 15632 USA
//    7;Siemens Schweiz AG (Formerly: Landis & Staefa Division Europe);Klaus Wächter, Global Standardization Manager;Building Technologies Division International Headquarters Gubelstrasse 22 6301 Zug Switzerland
//    8;Delta Controls;David Ritter, Senior Technical Lead;17850 56th Ave. Surrey, BC V3S 1C7 Canada
//    9;Siemens Schweiz AG;Klaus Wächter, Global Standardization Manager;Building Technologies Division International Headquarters Gubelstrasse 22 6301 Zug Switzerland
//    10;Schneider Electric;Kevin Sweeney, Manager, Controller Software;1 High Street North Andover, MA 01845 USA
//    11;TAC;Kevin Strohman, Commercial Technology;1354 Clifford Avenue Loves Park, IL 61132-2940 USA
//    12;Orion Analysis Corporation;Wayne Werner;PO Box 38408 Baltimore, MD 21231-8408 USA
//    13;Teletrol Systems Inc.;Richard Desmarais, Dir. of Engineering;286 Commercial Street Manchester, NH 03101 USA
//    14;Cimetrics Technology;Jim Butler;55 Temple Place Boston, MA 02111-1300 USA
//    15;Cornell University;H. Michael Newman;Facilities Operations Humphreys Service Building Ithaca, NY 14853-3701 USA
//    16;United Technologies Carrier;Mark Jones;Carrier Corporation 30 Batterson Park Road Farmington, CT 06034 USA
//    17;Honeywell Inc.;Anil Saigal;Commercial Buildings Group 1500 West Dundee Road Arlington Heights, IL 60004 USA
//    18;Alerton / Honeywell;Bill Swan, Engineering Fellow;6670 185th Avenue, NE Redmond, WA 98052 USA
//    19;TAC AB;Christer Scheja, Product Manager Communication;Jagershillgatan 18 S-213 75 Malmo Sweden
//    20;Hewlett-Packard Company;Jim Rounds, R&D Functional Manager;Lake Stevens Instrument Division 8600 Soper Hill Road MS 90 Everett, WA 98205-1298 USA
//    21;Dorsette’s Inc.;Fred A. Trivette, Jr., Operations Manager;100 Woodlyn Drive P.O. Box 1339 Yadkinville, NC 27055-1339 USA
//    22;Siemens Schweiz AG (Formerly: Cerberus AG);Klaus Wächter, Global Standardization Manager;Building Technologies Division International Headquarters Gubelstrasse 22 6301 Zug Switzerland
//    23;York Controls Group;Roger Woodward, Director of Controls;York International Mail Station 232Z P.O. Box 1592 York, PA 17405-1592 USA
//    24;Automated Logic Corporation;Mike Danner, Software Engineer;1150 Roberts Blvd. Kennesaw, GA 30144-3618 USA
//    25;CSI Control Systems International;Dick Cress;1625 W. Crosby Road Carrollton, TX 75006 USA
//    26;Phoenix Controls Corporation;Christopher Martin, Sr. Manager—Product Marketing;74 Discovery Way Acton, MA 01720 USA
//    27;Innovex Technologies, Inc.;Bruce D. Arnold, V.P., Engineering;526 Braddock Avenue Turtle Creek, PA 15145 USA
//    28;KMC Controls, Inc.;Wayne D. Kehler;P.O. Box 497 19476 Industrial Drive New Paris, IN 46553 USA
//    29;Xn Technologies, Inc.;Alan Hale, President;2416 Cheney-Spokane Road P.O. Box 350 Cheney, WA 99004-0350 USA
//    30;Hyundai Information Technology Co., Ltd.;Kang Myung Ung, Research Director;Industrial Automation Park R&D Center 1st Building, San 1-8, Mabuk-ri, Gusung-myun Yongin-si, Kyunggi-do 449-910 Korea
//    31;Tokimec Inc.;Kiyoshi Fujii, Senior Executive DIrector;16 Minam i-Kamat 2-chome Ohta-ku Tokyo 144 Japan
//    32;Simplex;Charles Winterble, Vice President Product Development;Simplex Plaza Gardner, MA 01441-0001 USA
//    33;North Building Technologies Limited;James Palmer, Business Development;P.O. Box 2673 Brighton BN1 3US United Kingdom
//    34;Notifier;Michael J. Lynch, Director of Business Development;12 Clintonville Road Northford, CT 06472-1653 USA
//    35;Reliable Controls Corporation;Thomas Zaban, P.E., VP Marketing;120 Hallowell Road Victoria, BC V9A 7K2 Canada
//    36;Tridium Inc.;Danny Wahlquist, VP;3951 Westerre Parkway Suite 350 Richmond, VA 23233 USA
//    37;Sierra Monitor Corporation;Richard Theron, Product Manager;1991 Tarob Court Milpitas, CA 95035 USA
//    38;Silicon Energy;Mike Hadley, VP Marketing;1250 Marina Village Parkway Suite 100 Alameda, CA 94501 USA
//    39;Kieback & Peter GmbH & Co KG;Hans Symanczik;Tempelhofer Weg 50 12347 Berlin Germany
//    40;Anacon Systems, Inc.;Bala Padmakumar, Pres. & CEO;1043 Shoreline Blvd. Suite 202 Mountain View, CA 94043 USA
//    41;Systems Controls & Instruments, LLC;H. Nelson Bender, VP Marketing/Sales;1325 Mink Road Perkasie, PA 18944 USA
//    42;Acuity Brands Lighting, Inc.;Rich Westrick, VP of Network Controls Engineering;One Lithonia Way Conyers, GA 30012 USA
//    43;Micropower Manufacturing;Derek Rowbotham, Tech. Director;8 Precision Street Kya-sand Randburg South Africa
//    44;Matrix Controls;Dana K. Wallace, Controls Special Project Manager;1015 South Green Street Tupelo, MS 38801 USA
//    45;METALAIRE;David John, Director of Marketing;1310 North Hercules Avenue Clearwater, FL 33765 USA
//    46;ESS Engineering;Cherisse Nicastro, Project Manager;64 East Broadway Road Suite 230 Tempe, AZ 85282 USA
//    47;Sphere Systems Pty Ltd.;Dr. Peter Hammer;ACN 006 579 004 15 McNamara Street Macleod, VIC 3085 Australia
//    48;Walker Technologies Corporation;Al Walker, President;3001 Moray Avenue, Suite B Courtenay, BC V9N 7S7 Canada
//    49;H I Solutions, Inc.;Rod Hampton, VP Engineering;4040 Royal Drive Kennesaw, GA 30144 USA
//    50;MBS GmbH;Martin Brust-Theiss, Company Leader;Roemerstrasse 15 47809 Krefeld Germany
//    51;SAMSON AG;Peter Opl;Postfach 10 19 01 D-60019 Frankfurt am Main Germany
//    52;Badger Meter Inc.;Craig V. Warne, Manager Electrical Engr. (Tulsa);6116 E. 15th St. Tulsa, OK 74112 USA
//    53;DAIKIN Industries Ltd.;Tohru Hirano, General Manager;Electronic Engineering Laboratory 1000-2 Okamoto-Cho Kusatu, Siga, 525-8526 Japan
//    54;NARA Controls Inc.;Jai-Woo Lee, General of R&D;70-10, Nonhyun-dong Kangnam-gu Seoul 135 010 Korea
//    55;Mammoth Inc.;Arthur Borland, Control Systems Product Manager;101 West 82nd Street Chaska, MN 55318-9663 USA
//    56;Liebert Corporation;Steven Ziejewski, Operations Manager;1050 Dearborn Drive Columbus, OH 43229 USA
//    57;SEMCO Incorporated;Richard Mitchell, Engineering Manager;1800 East Pointe Drive Columbus, MO 65201-3508 USA
//    58;Air Monitor Corporation;Paresh Dave, Manager, Applications Engineering;1050 Hopper Avenue Santa Rosa, CA 95403 USA
//    59;TRIATEK, LLC;James E. Hall, CEO;2150 Boggs Road Duluth, GA 30096 USA
//    60;NexLight;Eric Peterschmidt, VP of Sales;Northport Engineering, Inc. 953 South Concord P.O. Box 77 South St. Paul, MN 55075 USA
//    61;Multistack;Greg Michek;365 S. Oak Street West Salem, WI 54669 USA
//    62;TSI Incorporated;James Doubles, CEO;500 Cardigan Road P.O. Box 64394 St. Paul, MN 55164 USA
//    63;Weather-Rite, Inc.;Keith Kelly, President & CEO;616 N. 5th Street Minneapolis, MN 55401 USA
//    64;Dunham-Bush;Robert Kniss, Electrical Engineering Manager;101 Burgess Road Harrisonburg, VA 22801 USA
//    65;Reliance Electric;Kevin Fedor, VTAC 7 Drives Marketing Engineer;24800 Tungsten Road Euclid, OH 44117 USA
//    66;LCS Inc.;Richard C. White, VP;2259 Scranton Carbondale Highway Scranton, PA 18508 USA
//    67;Regulator Australia PTY Ltd.;Neil Kenny, Managing Director;A. C. N. 001 225 636 8 Hope Street Ermington Sydney, NSW 2115 Australia
//    68;Touch-Plate Lighting Controls;Doug Ford, President;Touchplate Technologies, Inc. 1830 Wayne Trace Fort Wayne, IN 46803 USA
//    69;Amann GmbH;Panjoerg Salzmann, Sales Manager;Untere Hauptstrasse 94 D-73630 Remshalden Germany
//    70;RLE Technologies;Donald A. Raymond, President/CEO;208 Commerce Drive Unit 3/C Fort Collins, CO 80524 USA
//    71;Cardkey Systems;Steve Platt, Principal Engineer;1757 Tapo Canyon Road Simi Valley, CA 93063 USA
//    72;SECOM Co., Ltd.;Kazuhiko Tsushima, Director;Shinjuku Nomura Building 1-26-2 Nishi-Shinjuku Shinjuku, Tokyo 163-0555 Japan
//    73;ABB Gebäudetechnik AG Bereich NetServ;Georg Setzer, Mgr. Building Automation Control Centers;Postfach 10 03 51 D-68128 Mannheim Germany
//    74;KNX Association cvba;Heinz Lux, Director;KNX Association cvba Bessenveldstraat 5 Brussels, Diegem B1831 Belgium
//    75;Institute of Electrical Installation Engineers of Japan (IEIEJ);Soji Ishiyama, Secretary General;1-12-5, Hongo, Bunkyo-ku Tokyo, 113-0033 Japan
//    76;Nohmi Bosai, Ltd.;Yukimasa Tachibana, Tech. Department Manager;7-3, Kudan Minami 4-Chome Chiyoda-Ku Tokyo, 102-8277 Japan
//    77;Carel S.p.A.;Simone Ravazzolo, Product Manager;B.U. Systems for AC/R Via dell'Industria, 11 35020 Brugine PD Italy
//    78;UTC Fire & Security España, S.L.;Charl du Plessis;Verge de Guadalupe, 3 Esplugues de Llobregat Barcelona 08950 Spain
//    79;Hochiki Corporation;Naoya Matsuoka, Manager;Software Development Division 246, Tsuruma, Machida-Shi Tokyo, 194-8577 Japan
//    80;Fr. Sauter AG;Roland Hofstetter, Product Management, Head of Automation Station and System Integration;Im Surinam 55 CH-4016 Basel Switzerland
//    81;Matsushita Electric Works, Ltd.;Keiji Haga, Director;Total Building Systems Department 1048, Kadoma, Kadoma-shi Osaka 571-8686 Japan
//    82;Mitsubishi Electric Corporation, Inazawa Works;Hiroshi Yoshikawa, Manager, Bldg. Systems Section;Inazawa Works No. 1 Hishi-Machi, Inazawa Aichi 492-8682 Japan
//    83;Mitsubishi Heavy Industries, Ltd.;Chuzo Ninagawa, Manager;Electronic Equipment Engineering Section Air-Conditioning & Refrigeration Systems HQ 3-1, Asahimachi Nishibiwajima-Cho Nishikasugai-Gun Aichi-Pref., 452-8561 Japan
//    84;Xylem, Inc.;James Gu, Controls and Instrumentation Supervisor;8200 N. Austin Avenue Morton Grove, IL 60053 USA
//    85;Yamatake Building Systems Co., Ltd.;Hiroshi Ito, Director of Product Development Department;Isehara Office 54 Suzukawa Isehara Kanagawa 259-1195 Japan
//    86;The Watt Stopper, Inc.;Doug Paton, Product Line Manager;6818 Patterson Pass Road Suites A&B Livermore, CA 94550 USA
//    87;Aichi Tokei Denki Co., Ltd.;Nobuyasu Murase, System Department Manager, System Division;5-10, Futano-cho, Mizuho-ku Nagoya 467-0861 Japan
//    88;Activation Technologies, LLC;Steven Fey;31 Pelham Road Salem, NH 03079 USA
//    89;Saia-Burgess Controls, Ltd.;Joachim Krusch, Head of Technical Marketing Group Europe;Bahnhofstrasse 18 CH-3289 Murten Switzerland
//    90;Hitachi, Ltd.;Nobuhisa Kobayashi, Manager of Building Systems Engineering Dept.;1070 Ichige, Hitachinaka-shi Ibaraki-ken, 312-8506 Japan
//    91;Novar Corp./Trend Control Systems Ltd.;Alan Carter, Head of New Product Development;P.O. Box 34 Horsham, West Sussex RH12 2YF United Kingdom
//    92;Mitsubishi Electric Lighting Corporation;Kiyoshi Nakamura, Manager, System Lighting Technical Section;2-14-40 Ofuna Kamakura City, Kanagawa Prefecture 247-0056 Japan
//    93;Argus Control Systems, Ltd.;David Flood, Manager, Research & Development;1281 Johnston Road White Rock, BC V4B 3Y9 Canada
//    94;Kyuki Corporation;Hiroshi Okada, Dep. Mgr., Marketing & Engineering Division;4-19-18 Shimizu Minami-Ku Fukouka City Fukouka 915-0031 Japan
//    95;Richards-Zeta Building Intelligence, Inc.;Edmund B. Richards, Owner;6326 Lindmar Drive Santa Barbara, CA 93117 USA
//    96;Scientech R&D, Inc.;Jean-Pierre Drolet, Ph.D., Director, Software R&D;247, rue Thibeau Cap-de-la-Madeleine Quebec G8T 6X9 Canada
//    97;VCI Controls, Inc.;Bruce M. Dolan, Marketing Manager;14 Capella Court Ottawa, Ontario, K2E 7V6 Canada
//    98;Toshiba Corporation;Kazumasa Uchida, Mgr. of Building Systems Solution Group;Fuchu Operations  Social Infrastructure Systems 1 Toshiba-cho, Fuchu-shi Tokyo 183-8511 Japan
//    99;Mitsubishi Electric Corporation Air Conditioning & Refrigeration Systems Works;Masami Iwahashi, System Control Section;6-5-66 Tebira, Wakayama City Wakayama Prefecture 640-8686 Japan
//    100;Custom Mechanical Equipment, LLC;Dick Peitz, President;2080 Energy Drive East Troy, WI 53120 USA
//    101;ClimateMaster;Dennis Meyer, Product Manager;2375 E. Terrace Drive Bluffton, IN 46714 USA
//    102;ICP Panel-Tec, Inc.;Lane Ingram, CEO;2607 Leeman Ferry Road Huntsville, AL 35801 USA
//    103;D-Tek Controls;Ed Veldman, CEO;P.O. Box 246 Monarch, Alberta T0L 1M0 Canada
//    104;NEC Engineering, Ltd.;Hideo Takahashi, Senior Manager;4388 Ikebe-cho, Tuduki-ku 224-0053 Yokohama City, Kanagawa Japan
//    105;PRIVA BV;Marco Polet, Project Manager R&D Software;Zijlweg 3 P.O. Box 18 2678 ZG De Lier Netherlands
//    106;Meidensha Corporation;Akinobu Kobayashi, Gen'l Mgr. of Public Facilities Engineering Division;Riverside Building, 36-2 Nihonbashi Hakozakicho Chuo-ku, Tokyo 103-8515 Japan
//    107;JCI Systems Integration Services;Tim Gooch, Project Manager;9410 Bunsen Parkway Louisville, KY 40220 USA
//    108;Freedom Corporation;Toshi Uehara, Manager;1-24-11 Shinmachi Nishi-ku Osaka 550-0013 Japan
//    109;Neuberger Gebäudeautomation GmbH;Klaus Lenkner, Company Leader;Oberer Kaiserweg 6 91541 Rothenburg ob der Taube Germany
//    110;eZi Controls;Craig Potter, Engineering Manager;41 Willowbrook Office Park Block C, Van Hoof Street Ruimsig, 1724 South Africa
//    111;Leviton Manufacturing;Robert Hick, VP Research & Development;Lighting Controls Division 20497 SW Teton Tualatin, OR 97062 USA
//    112;Fujitsu Limited;Munetsugu Takauchi, Public Network Solution Dept. Director;Public Security Solution Group Solution Development Division 4-1-1 Kamikodanaka, Nakahara-ku Kawasaki, Kanagawa 211-8588 Japan
//    113;Vertiv (Formerly Emerson Network Power);Darryl Brown, Product Manager;975 Pittsburg Drive Delaware, OH 43015 USA
//    114;S. A. Armstrong, Ltd.;Gaby Haddad, Product Development Specialist;23 Bertrand Avenue Toronto, ON M1L 2P3 Canada
//    115;Visonet AG;Norbert Kraus, Software Development;Baarerstrasse 101 Zug 6300 Switzerland
//    116;M&M Systems, Inc.;Paul A. Valentine;4 W. Tower Circle Ormond Beach, FL 32174 USA
//    117;Custom Software Engineering;Ronald Davis, Owner;442 Autumn Springs Drive Avon, IN 46123 USA
//    118;Nittan Company, Limited;Minori Miyagawa, General Manager, Sales Eng. Dept.;11-6, Hatagaya 1-Chome, Shibuya-ku Tokyo 151-8535 Japan
//    119;Elutions Inc. (Wizcon Systems SAS);Emmanuel Vitrac, Director of Marketing, Europe & Asia;Parc Technologique de Lyon 12, Allee Irene Joliot Curie - Bat. B1 F-69791 Saint-Priest Cedex France
//    120;Pacom Systems Pty., Ltd.;Scott Finneran;Unit 6, 40 Carrington Road Castle Hill, NSW 2154 Australia
//    121;Unico, Inc.;Harry Schulz, Sr. Development Engineer;3725 Nicholson Road P.O. Box 0505 Franksville, WI 53126 USA
//    122;Ebtron, Inc.;Michael J. Urbaniak, Senior Vice-President;1663 Highway 701 South Loris, SC 29569 USA
//    123;Scada Engine;Chris Gurtler, President;27 Sunnyside Grove Bentleigh 3204 Victoria Australia
//    124;Lenze Americas (Formerly: AC Technology Corporation);Michael Mamro, Engineering Manager SW & Controls;630 Douglas Street Uxbridge, MA 01569 USA
//    125;Eagle Technology;Harshad Shah, President;11019 N Towne Square Rd Mequon, WI 53092 USA
//    126;Data Aire, Inc.;Minh Tran, Senior Controls Engineer;230 W. Blueridge Avenue Orange, CA 92865 USA
//    127;ABB, Inc.;Tim Skell, Lead Application Engineer;16250 W. Glendale New Berlin, WI 53151 USA
//    128;Transbit Sp. z o. o.;Frank Wlodzimierz, President;ul. Przyczólkowa 109 A 02-968 Warszawa Poland
//    129;Toshiba Carrier Corporation;Ichiro Honda, Gen'l Mgr, System Solution Engineering Development Group;336 Tadewara Fuji, Shizuoka 416-8521 Japan
//    130;Shenzhen Junzhi Hi-Tech Co., Ltd.;Zhengyuan Xu, General Manager;23A West, Xinghe Huaju Building Futian District, Shenzhen City 518045 China
//    131;Tokai Soft;Matsura Akira;2-15-1 Shinmichi Nishi-ku Nagoya 451-0043 Japan
//    132;Blue Ridge Technologies;Ron Poskevich, General Manager;1800 Sandy Plains Industrial Parkway Suite 216 Marietta, GA 30066 USA
//    133;Veris Industries;Marc Bowman, VP Engineering;12345 SW Leveton Drive Tualatin, OR 97062 USA
//    134;Centaurus Prime;Robert D. Caldwell, President;4425 Cass Street, Suite A San Diego, CA 92109 USA
//    135;Sand Network Systems;Hans J. Lau, President;434 Payran Street Petaluma, CA 94952 USA
//    136;Regulvar, Inc.;Eric Boisclair, Analyst;1985, Boulevard Industriel Laval, Quebec H7S 1P6 Canada
//    137;AFDtek Division of Fastek International Inc.;Ashraf Ali, P. Eng. President;245 Riviera Drive Unit 1 Markham, ON L3R5J9 Canada
//    138;PowerCold Comfort Air Solutions, Inc.;Curt Musser, VP of PowerCold Energy Systems;12345 Starkey Road, Suite A Largo, FL 33773 USA
//    139;I Controls;Hyun Jeong, Vice President;2F #302, I'Park 11, Jeongja-dong, Bundang-gu Seongnam-City, Gyeonggi-do Korea
//    140;Viconics Electronics, Inc.;David Durian, Engineering Manager;9245 Langelier Blvd. St. Leonard, Quebec H1P 3K9 Canada
//    141;Yaskawa America, Inc.;Ron Fox, Senior Development Engineer;Drives & Motion Division 2121 Norman Drive South Waukegan, IL 60085 USA
//    142;DEOS control systems GmbH;Martin Beckmann, Sales Director;Birkenallee 113 D-48432 Rheine Germany
//    143;Digitale Mess- und Steuersysteme AG;Volker Wucher, Software Development;D-76275 Ettlingen Germany
//    144;Fujitsu General Limited;Naoki Aihara, Manager of AC System Engineering Division;3-3-17 Suenaga, Takatsu-ku Kawasaki 213 8502 Japan
//    145;Project Engineering S.r.l.;David Ricci;Via Colle Ramole, 11 50029 Tavarnuzze - Impruneta (Fl) Italy
//    146;Sanyo Electric Co., Ltd.;Sachiyoshi Ukai, Manager;Commercial Technology Headquarters Engineering Planning BU 1-1-1 Sakata, Oizumi Ora-gun, Gunma 370-0596 Japan
//    147;Integrated Information Systems, Inc.;Dan Bellows, CEO;Forest Lake Office 22965 Imperial Avenue North Forest Lake, MN 55025 USA
//    148;Temco Controls, Ltd.;Maurice Duteau, Manager Director;3101 Mount Granite Court West Richland, WA 99353 USA
//    149;Airtek International Inc.;Bill Chao, President;19 Fl.-4, No. 77 Hsin Tai Wu Road, Sec. 1 Taipei, Taiwan China
//    150;Advantech Corporation;Michael Rothwell, Director, Marketing & eAutomation Engineering Center;Industrial Automation Group 1320 Kemper Meadow Drive, Suite 500 Cincinnati, OH 45240 USA
//    151;Titan Products, Ltd.;Iain Twiss, Technical Manager;15 Lathan Close Bredbury Park Industrial Estate Stockport SK6 2SP United Kingdom
//    152;Regel Partners;Arien Peterse, Manager;Hogerbrinkerweg 8, 3871 KN Hoevelaken Postbox 86, 3870 CB Hoevelaken Netherlands
//    153;National Environmental Product;Christian Renaud, Design Engineer;400 Lebeau Boulevard Montreal, Quebec Canada
//    154;Unitec Corporation;Shogo Nasu, CEO;24 Uchiwariden-Ichinotori, Kisogawa Ichinomiya, Aich 493-0006 Japan
//    155;Kanden Engineering Company;Masayuki Tsuji, General Manager, System Solution Dept.;6-2-27, Nakanoshima, Kita-ku Osaka 530-6691 Japan
//    156;Messner Gebäudetechnik GmbH;Volker Barufke;Weidenweg 13 Erlangen, D91058 Germany
//    157;Integrated.CH;Stephen Wreford-Doree;Allenbergstrasse 19 8708 Mannedorf Switzerland
//    158;Price Industries;Mike Nicholson, Electronic Development Center Manager;638 Raleigh Street Winnipeg, Manitoba R2K 3Z9 Canada
//    159;SE-Elektronic GmbH;Hermann Lippert;Eythstrasse 16 Göppingen, 73037 Germany
//    160;Rockwell Automation;Scott D. Braun, Manager, Communications Products, Drives Business;6400 West Enterprise Drive Mequon, WI 53092 USA
//    161;Enflex Corp.;Karl T. C. Swanson, Operations;1040 Whipple Street Suite 225 Prescott, AZ 86305 USA
//    162;ASI Controls;Mashuri L. Warren, Director of Product Development;2202 Camino Ramon San Ramon, CA 94583-1339 USA
//    163;SysMik GmbH Dresden;Gert-Ulrich Vack, General Manager;Bertolt-Brecht-Allee 24 Dresden, 01309 Germany
//    164;HSC Regelungstechnik GmbH;Ulrich Pink;An der Lehmkaute 13 Bad Marienberg, 56470 Germany
//    165;Smart Temp Australia Pty. Ltd.;Peter Symons, Director;19 Indra Road Blackburn South, Victoria 3130 Australia
//    166;Cooper Controls;David Viveiros, Product Manager;6 Green Tree Drive S. Burlington, VT 05403 USA
//    167;Duksan Mecasys Co., Ltd.;Jihyung Kim, Chief Technology Officer;13F/The 8th Ace TechnoTower 181-7 Kuro-3-Dong Kuro-Gu Seoul Korea
//    168;Fuji IT Co., Ltd.;Kazutaka Kohata, Staff Manager;Plant Facility Solution Dept. TIS Building 4-3, Akebono-cho 2-Chome Tachikawa City, Tokyo 190-0012 Japan
//    169;Vacon Plc;Jani Smeds, Development Engineer;Runsorintie 7 Vaasa, 65380 Finland
//    170;Leader Controls;Lin Xing Yuan, General Manager;17 East Rong Jing Road Beijing Developed Area YiZhuang 100176 China
//    171;Cylon Controls, Ltd.;Malachy Lynch, Firmware Engineer;Clonshaugh Business and Technology Park Clonshaugh, Dublin 17 Ireland
//    172;Compas;Lesnek Skreta, Technical Director;ul. Modlinska 17B Jablonna, 05-110 Poland
//    173;Mitsubishi Electric Building Techno-Service Co., Ltd.;Hiroyasu Tabata, Assistant Manager;19-1 Arakawa 7-chome Arakawa-ku, Tokyo 116-0002 Japan
//    174;Building Control Integrators;Jonathan P. Fulton, President;4490 Edgewyn Avenue Hilliard, OH 43026 USA
//    175;ITG Worldwide (M) Sdn Bhd;Chan Weng Tong, Senior Manager;2, Jalan Astaka U8/83 Seksyen U8 Bukit Jelutong, Shah Alam 40150 Malaysia
//    176;Lutron Electronics Co., Inc.;Scott Ziegenfus, Senior Applications Engineer;7200 Suter Road Coopersburg, PA 18036 USA
//    177;Cooper-Atkins Corporation;John Greene, Director of Technology Development;33 Reeds Gap Road Middlefield, CT 06455 USA
//    178;LOYTEC Electronics GmbH;Dietmar Loy;Stolzenthalergasse 24/3 Vienna A-1080 Austria
//    179;ProLon;Marc Bergeron;1989 Michelin Street Laval, Quebec H7L 5B7 Canada
//    180;Mega Controls Limited;Eric Ho, General Manager;Room 1521A, Star House 3 Salisbury Road Tsimshatsui, Kowloon China
//    181;Micro Control Systems, Inc.;Brian Walterick, President;5877 Enterprise Parkway Ft. Myers, FL 33095 USA
//    182;Kiyon, Inc.;James B. Rimmer, Member of Technical Staff;4225 Executive Square, Suite 290 La Jolla, CA 92037 USA
//    183;Dust Networks;Pedro Rump, VP Engineering;30695 Huntwood Avenue Hayward, CA 94544 USA
//    184;Advanced Building Automation Systems;Bryan Grayson, R&D Manager;8 Harley Crescent Condell Park NSW 2200 Milperra, DC NSW 1891 Australia
//    185;Hermos AG;Andreas Heisel, Manager of Software Development;Gartenstrasse 19 Mistelgau, 95490 Germany
//    186;CEZIM;Piotr Golabek, Product Manager;ul. Partyzantow 1 Sochaczew, 96-500 Poland
//    187;Softing;Achim Liebl, Head of Development;Richard Reitzner Allee 6 Haar, D-85540 Germany
//    188;Lynxspring, Inc.;Shawn Jocobson, Director of Application Development;1210 NE Windsor Drive Lee’s Summit, MO 64086 USA
//    189;Schneider Toshiba Inverter Europe;Jean-Pierre Guilmeau, Vice-President Marketing;Rue Andre Blanchet Pacy Sur Eure, F-27120 France
//    190;Danfoss Drives A/S;Ove Wiuff, Product Manager;Ulsnaes 1, Office DG 596 DK, Graasten 6300 Denmark
//    191;Eaton Corporation;Mark Verheyen, Principal Engineer;4201 North 27th Street Milwaukee, WI 53216 USA
//    192;Matyca S.A.;Juan Leni, Director Engineering;Av. Colon 423 Piso 1 of 5 Mendoza, 5500 Argentina
//    193;Botech AB;Henrik Johansson;Ledebursgatan 5 Malmö, SE-211 55 Sweden
//    194;Noveo, Inc.;Jean-Pierre Dionne, President;10581 Louis H. Lafontaine Blvd. Montreal, Quebec H1J 2E8 Canada
//    195;AMEV;Bernhard Hall, AMEV - Geschäftsstelle;Krausenstraße 17-20 Berlin, D-10117 Germany
//    196;Yokogawa Electric Corporation;Hiroaki Tanaka, Senior Engineer;9-32, Nakacho 2-chome Musashino-shi Tokyo 180-8750 Japan
//    197;GFR Gesellschaft für Regelungstechnik;Volker Westerheide, Managing Director;Kapellenweg 42 Verl, D-33415 Germany
//    198;Exact Logic;Matthew Barlage, Product Development Engineer;5600 Queens Avenue, NE Suite 400 Otsego, MN 55330 USA
//    199;Mass Electronics Pty Ltd dba Innotech Control Systems Australia;Stephen A. Miranda, Managing Director;6 McKechnie Drive Eight Mile Plains Brisbane, Queensland 4113 Australia
//    200;Kandenko Co., Ltd.;Makoto Kojima, Deputy General Manager of Engineering Department;Business Control Division 4-8-33 Shibaura Minato-Ku, Tokyo 108-8533 Japan
//    201;DTF, Daten-Technik Fries;Wolfgang Fries;Hochstrasse 25 Dachau, D-85221 Germany
//    202;Klimasoft, Ltd.;Ivan Svancara, Director;Vajanskeho 58 Piestany, 921 01 Slovakia
//    203;Toshiba Schneider Inverter Corporation;Hiromichi Nishimura, Senior Specialist;Development and Design Group 2121, Nao, Asahi-Cho Mie-Gun, Mie 510-8521 Japan
//    204;Control Applications, Ltd.;Doron Matatyahu, Development Manager;24 A Habarzel Street Tel Aviv, 69710 Israel
//    205;CIMON CO., Ltd.;Jae-hwan Lee;KDT B/D, 48, Beolmal-ro Bundang-gu Seongnam-si, Gyeonggi-do Korea
//    206;Onicon Incorporated;David B. Johnston, Product Manager;1500 North Belcher Road Clearwater, FL 33765 USA
//    207;Automation Displays, Inc.;Robert P. Lowry, Electrical Engineer;3533 N. White Avenue Eau Claire, WI 54703 USA
//    208;Control Solutions, Inc.;James G. Hogenson, President;2179 Fourth Street, Ste. 2-G PO Box 10789 White Bear Lake, MN 55110-0789 USA
//    209;Remsdaq Limited;Martyn J. Taylor, Technical Director;Parkway, Deeside Industrial Park Deeside, Flintshire CH5 2NL United Kingdom
//    210;NTT Facilities, Inc.;Kazuo Oshima, Executive Manager;Research and Development Headquarters GHY Building, 2-12-1 Kitaotsuka Toshima-ku, Tokyo 170-0004 Japan
//    211;VIPA GmbH;Markus Krauss;Gesellschaft für Visualisiersung und Prozessautomatisierung mbH Ohmstrasse 4 Herzogenaurach, 91074 Germany
//    212;TSC21 Association of Japan;Ryuji Yanagihara, Manager;3-47-8-208 Koenji-Minami Suginami-ku, Tokyo 166-0003 Japan
//    213;Strato Automation;Andre Baril, President;6781 Rue Bombardier Saint-Leonard, Québec H1P 2W2 Canada
//    214;HRW Limited;Greg Keall, Managing Director;Unit 505, 5/F Wai Wah Commercial Centre 6 Wilmer Street Sai Ying Pun, Hong Kong China
//    215;Lighting Control & Design, Inc.;David J. Long, Software Engineer;PO Box 250 Wrightwood, CA 92397 USA
//    216;Mercy Electronic and Electrical Industries;John D. Maniraj, CEO;503 Naganapalaya Maruthi Sevanagar, Bangalore 560 033 India
//    217;Samsung SDS Co., Ltd;Woon-Hak Paek, General Manager of IBS Development Division;Samsung SDS 2nd Building 4th Floor 159-9 Gumi-Dong Bundang-Gu, Seongnam-Si, Gyeonggi-Do 463-810 Korea
//    218;Impact Facility Solutions, Inc.;Keith E. Gipson, CTO;417 Arden Avenue, Ste. 116 Glendale, CA 91203 USA
//    219;Aircuity;Scott Sitterly, Systems Integration Manager;39 Chapel Street Newton, MA 02458 USA
//    220;Control Techniques, Ltd.;Stephen Turner, Development Engineer;The Gro, Newtown Powys, SY16 3BE United Kingdom
//    221;OpenGeneral Pty., Ltd.;William Oldjohn, Managing Director;2 Shearson Crescent Mentone, Victoria 3194 Australia
//    222;WAGO Kontakttechnik GmbH & Co. KG;Thomas Albers, Technical Director Electronics;Hansastraße 27 Minden, 32423 Germany
//    223;Cerus Industrial;Kent Holce, CEO;3101 SW 153rd Drive, Suite 318 Beaverton, OR 97007 USA
//    224;Chloride Power Protection Company;Doug Griffin, Lead Software Engineer;27944 N. Bradle Road Libertyville, IL 60048 USA
//    225;Computrols, Inc.;Drew Mire, Vice President of Operations;2520 Belle Chasse Highway Gretna, LA 70053 USA
//    226;Phoenix Contact GmbH & Co. KG;Thomas Erben;Flachsmarkstrasse 8 Blomberg, 32825 Germany
//    227;Grundfos Management A/S;Henrik Frederiksen, Product Manager Data Communication;Poul Due Jensens Vej 7 Bjerringbro, DK-8850 Denmark
//    228;Ridder Drive Systems;Paul Oudraad, Manager R & D;PO Box 360 AJ Harderwijk, 3840 Netherlands
//    229;Soft Device SDN BHD;Pang Wai Yeen;No 10 Jin DBP Dolomite Business Park Batu Caves Selangor 68100 Malaysia
//    230;Integrated Control Technology Limited;Hayden Burr, Director;PO Box 302-340 North Harbour Post Centre Auckland, 0725 New Zealand
//    231;AIRxpert Systems, Inc.;Stephen A. Wallis, President;1 John Wilson Lane Lexington, MA 02421 USA
//    232;Microtrol Limited;Mike Baker, Director;16 Elgar Business Centre Moseley Road Hallow, Worcester, Worcestershire WR2 6NJ United Kingdom
//    233;Red Lion Controls;Mike Granby, President;20 Willow Springs Circle York, PA 17402 USA
//    234;Digital Electronics Corporation;Yoshiyuki Murata, General Manager;8-2-52 Nanko-higashi Suminoe-ku, Osaka 559-0031 Japan
//    235;Ennovatis GmbH;Hartmut Freihofer, Director;Stammheimer Strasse 10 Kornwestheim, 70806 Germany
//    236;Serotonin Software Technologies, Inc.;Matthew Lohbihler, President;90 Castleglen Blvd. Toronto, ON L6C 0B3 Canada
//    237;LS Industrial Systems Co., Ltd.;Kiyeon Kim;Yonsei Jaedan Severance Bldg. 84-11 5ga Namdaemun-ro Jung-gu, Seoul 100-753 Korea
//    238;Square D Company;Drew Reid, Product Manager, Lighting Control Business;295 Tech Park Drvie Suite 100 LaVergne, TN 37086 USA
//    239;S Squared Innovations, Inc.;Sam Saprunoff, President;6807-104 Street Edmonton, AB T6H 2L5 Canada
//    240;Aricent Ltd.;Bhuvnesh Sharma, Business Director - Devices Group;18/1 Outer Ring Road Panathur Post Bangalore, 560 087 India
//    241;EtherMetrics, LLC;Kenneth Barclay, President;120 Washington Avenue Albany, NY 12210 USA
//    242;Industrial Control Communications, Inc.;Darrin Hansen, President;1600 Aspen Commons, Suite 210 Middleton, WI 53562 USA
//    243;Paragon Controls, Inc.;Larry Winterbourne, Vice President;2371 Circadian Way Santa Rosa, CA 95407 USA
//    244;A. O. Smith Corporation;Tom Van Sistine, Principal Engineer;Corporate Technology Center 12100 W. Park Place Milwaukee, WI 53224-9152 USA
//    245;Contemporary Control Systems, Inc.;Bennet Levine, R&D Manager;2431 Curtiss Street Downers Grove, IL 60515 USA
//    246;Intesis Software SLU;Josep Ceron, General Manager;Mila I Fontanals 1 bis Igualada, Barcelona 08700 Spain
//    247;Ingenieurgesellschaft N. Hartleb mbH;Norbert Hartleb;Am Tarnenstumpf 17 Dreieich, D-63303 Germany
//    248;Heat-Timer Corporation;Dan Shprecher, Chief Engineer;20 New Dutch Lane Fairfield, NJ 07004 USA
//    249;Ingrasys Technology, Inc.;Watson Wu, Senior Specialist;Business Development Division 21F, 207 Fu Hsing Road Taoyuan, Taiwan 33066 China
//    250;Costerm Building Automation;David E. Matthews, Sales & Marketing Manager;Kompasstraat 7 2901 AM Capelle aan de Ijssel Netherlands
//    251;WILO SE;Stephan Greitzke, Head of Engineering;Nortkirchenstrasse 100 Dortmund 44263 Germany
//    252;Embedia Technologies Corp.;Andrew Chu, Chief Technology Officer;Box 51027 Beddington RPO Calgary, Alberta T3K 3V9 Canada
//    253;Technilog;Patrick Goddefroy, Sales and Marketing Manager;Centre Val Courcelle 4 Route de la Noue Gif-sur-yvette, Cedex 91196 France
//    254;HR Controls Ltd. & Co. KG;Roul Placzek, Director;Head Office, Lilienstrasse 6 Erkrath, 40699 Germany
//    255;Lennox International, Inc.;Krishna Doddamane, Senior Staff Engineer;1600 Metrocrest Drive Carrollton, TX 75006 USA
//    256;RK-Tec Rauchklappen-Steuerungssysteme GmbH & Co. KG;Reiner Dunwald;Robert-Perthel-Straße 45 D-50739 Köln Germany
//    257;Thermomax, Ltd.;Patrick Davis, R&D Manager;7 Balloo Crescent Balloo Industrial Estate Bangor, Co. Down BT19 7UP United Kingdom
//    258;ELCON Electronic Control, Ltd.;Tibor Erdelyi, General Manager;Ihasz u. 10 Budapest, H-1105 Hungary
//    259;Larmia Control AB;Hans Bolov, President;Box 83 Sollentuna, SE-191 22 Sweden
//    260;BACnet Stack at SourceForge;Steve Karg, Developer;2765 Stanton Woods Drive, SE Conyers, GA 30094 USA
//    261;G4S Security Services A/S;Knud Danielsen, Product Manager;Roskildevej 157 Albertslund, DK-2620 Denmark
//    262;Exor International S.p.A.;Claudio Urbani, Product Manager HMI;Via Monte Fiorino, 9 37057 San Giovanni Lupatoto Verona Italy
//    263;Cristal Controles;Martin Labbe, R&D Manager;2025, rue Lavoisier, #135 Quebec, QC G1N 4L6 Canada
//    264;Regin AB;Urban Fosseus, Senior Software Designer;Box 366, SE-261 51 Landskrona, Sweden
//    265;Dimension Software, Inc.;Shawn Overcash, President;1536 St. Clair Road Taylorsville, NC 28681 USA
//    266;SynapSense Corporation;Kurt Sowa, Director of Software Engineering;2365 Iron Point Road, Ste. 100 Folsom, CA 95630 USA
//    267;Beijing Nantree Electronic Co., Ltd.;Goober Zhou, R&D Manager;No. 29 Dongbeiwang South Road Room 615 Haiden District, Beijing China
//    268;Camus Hydronics Ltd.;David Procunier, Assistant Engineer;6226 Netherhart Road Mississauga, L5T 1B7 Canada
//    269;Kawasaki Heavy Industries, Ltd.;Mutsuji Sakai, Manager of Electrical & Control Eng. Section;8, Niijima Harima-cho Kako-gun Hyougo Pref., 675-0180 Japan
//    270;Critical Environment Technologies;Laura Donahue, Purchasing Agent;Unit 145-7391 Vantage Way Delta, British Columbia V4G 1M3 Canada
//    271;ILSHIN IBS Co., Ltd.;Young Kwak, Manager, Applications Engineering;Dongkuk B/D A-412 83-6 Gochuck-dong Guro-gu, Seoul 152-827 Korea
//    272;ELESTA Energy Control AG;Angelo Quadroni, General Manager;Elestastrasse 18 Bad Ragaz CH-7310 Switzerland
//    273;KROPMAN Installatietechniek;Ir. J.A.J. (Joep) van der Velden, Landelijk Manager Meet- en Regeltechniek;Postbus 6705 6503 GE Nijmegen Netherlands
//    274;Baldor Electric Company;Michael S. Thomas, Design Engineer, Control Systems;600 South Zero Ft. Smith, AR 72908 USA
//    275;INGA mbH;Florian Kienast, Manager for SCADA Systems / Dipl.-Ing.;Wehler Weg 14 Hameln, D-31785 Germany
//    276;GE Consumer & Industrial;Paul E. Bunnell, Product Manager-Standard Drives and Soft Starts;41 Woodford Avenue Plainville, CT 06062 USA
//    277;Functional Devices, Inc.;Kenneth W. Rittmann, President;310 S. Union Street Russiaville, IN 46979 USA
//    278;StudioSC;Salvatore Cataldi, Technical Manager;Via Agostino da Montefeltro, 2 10134 Torino Italy
//    279;M-System Co., Ltd.;Koichi Yoshimura, Assistant Manager, R&D Department;5-2-55, Minamitsumori Nishinari-Ku, Osaka 557-0063 Japan
//    280;Yokota Co., Ltd.;Masato Kuwana, Assistant Manager, Shiga Factory;Product Development Dept. 45 Yokomizo-cho Higashiomi, Shiga 527-0135 Japan
//    281;Hitranse Technology Co., LTD;Zhao Gaozuo, General Manager;Building A, Room 316 18 Xihuan nanlu Economic-Tech. Development Area Beijing, PRC China
//    282;Vigilent Corporation;Cliff Federspiel;2001 Broadway, Fourth Floor Oakland, CA 94612 USA
//    283;Kele, Inc.;Lisa Kisil-Dense, Electrical Products Business Unit Manager;3300 Brother Blvd. Bartlett, TN 38133 USA
//    284;Opera Electronics, Inc.;Keith Rasmussen, President;77 Kirkwood Street Beaconsfield, Montreal H9W 5L3 Canada
//    285;Gentec;Marcel Landry, VP Division RVD;2625 Dalton Street Quebec, G1P 3S9 Canada
//    286;Embedded Science Labs, LLC;David A. Bruno, CTO;11755 SW Summer Crest Drive Tigard, OR 97223 USA
//    287;Parker Hannifin Corporation;Steven Schnelle, Engineering Manager, Climate Systems Division;10801 Rose Avenue New Haven, IN 46774 USA
//    288;MaCaPS International Limited;L.M. Cheng, CEO;Unit 8, 14/F., Block B, Hoi Luen Industrial Centre 55 Hoi Yuen Road Kwun Tong, Kowloon Hong Kong
//    289;Link4 Corporation;Yen Pham, President;1232 Village Way, Suite K Santa Ana, CA 92705 USA
//    290;Romutec Steuer-u. Regelsysteme GmbH;Stefan Kister, Quality Manager;Waidlachstr. 2 Buch am Wald, D-91592 Germany
//    291;Pribusin, Inc.;Michael Gerstweiler, Vice President;743 Marquette Avenue Muskegon, MI 49442 USA
//    292;Advantage Controls;Chris Liebig, System Administrator;4700 Harold Abitz Drive Muskogee, OK 74403 USA
//    293;Critical Room Control;Sam Sidhom;210 North Second Street Suite 010 Minneapolis, MN 55401 USA
//    294;LEGRAND;Jean-Luc Citerne, Services & Integrated Sys. Team Mgr.;128 Avenue de Lattre de Tassigny Limoges, Cedex 87045 France
//    295;Tongdy Control Technology Co., Ltd.;Xiaoyan Xi, Marketing Manager;3-3-5/f IN-DO MANSION No. jia-48 Zhichun Road Beijing, 100086 China
//    296;ISSARO Integrierte Systemtechnik;Maik Sauerlaender, Technical Engineer;Maik Sauerlaender & Sorren Rosengarrd GbR Breite Str. 69 Bernburg, 06406 Germany
//    297;Pro-Dev Industries;Peter Hertrick, Systems Development Engineer;PO Box 1267 Indooroopilly, Queensland 4068 Australia
//    298;DRI-STEEM;Jennifer Schroer, Program Manager;14949 Technology Drive Eden Prairie, MN 55344 USA
//    299;Creative Electronic GmbH;Reiner H. Schmahl, CEO;Lorcher Strasse 52 Birenbach, 73102 Germany
//    300;Swegon AB;Mattias Hedfjard, Development Engineer, Electrical Components;Box 300 Kvanum, SE-535 23 Sweden
//    301;FIRVENA s.r.o.;Jan Brachacek;Zamecke nam. 26 738 01 Frydek-Mistek Czech Republic
//    302;Hitachi Appliances, Inc.;Keiji Sato, Chief Engineer;390, Muramatsu, Shimizu-ku Shizuoka-shi, 424-0926 Japan
//    303;Real Time Automation, Inc.;Jeff Stiefvater, VP Operations;150 S. Sunny Slope Road, Suite 130 Brookfield, WI 53005 USA
//    304;ITEC Hankyu-Hanshin Co.;Hiroaki Aoyama, Manager, Computer System Dept.;Hanshin-Noda Center Bldg. 1-31 Ebie 1-chome Fukushima-ku, Osaka 553-0001 Japan
//    305;Cyrus E&M Engineering Co., Ltd.;Cedric Lee, Exporting Manager;Flat H, 3/F., Yue Cheung Center 1-3 Wong Chuk Yeung Street Fotan, Hong Kong China
//    306;Badger Meter;William Roeber, Principal Managing Engineer;8635 Washington Avenue  Racine, WI 53406 USA
//    307;Cirrascale Corporation;Victor Tung, VP, Engineering;9449 Carroll Park Drive San Diego, CA 92121-5202 USA
//    308;Elesta GmbH Building Automation;Urs Beck, Managing Director;Gottlieb-Daimler-Str. 1 Konstanz, D-78467 Germany
//    309;Securiton;Horst Geiser, Product Development Manager;Von-Drais-Strasse 33 Achern, D-77855 Germany
//    310;OSlsoft, Inc.;Jon H. Peterson, Vice President of Engineering;777 Davis Street, Ste. 250  San Leandro, CA 94577 USA
//    311;Hanazeder Electronic GmbH;Erwin Hanazeder, General Manager;Johann Michael Dimmelstr. 10  Reid im Innkreis, A-4910  Austria
//    312;Honeywell Security Deutschland, Novar GmbH;Andrea Bergerhoff, Managing Director;Johannes-Mauthe-Str. 14 Albstadt, D-72458  Germany
//    313;Siemens Industry, Inc.;Ken Rempe, P.E., Energy Management Division;5400 Triangle Parkway Norcross, GA 30092-2540 USA
//    314;ETM Professional Control GmbH;Klaus Jandl, Head of Technology;A Siemens Company  Kasernenstraße 29  Eisenstadt, A-7000 Austria
//    315;Meitav-tec, Ltd.;Elan Roy, R&D & Engineering Manager;6 Sapir Street, New Industrial Zone POB 5221 Rishon-LeZion, 75150 Israel
//    316;Janitza Electronics GmbH;Markus Janitza, Director;Vor dem Polstück 1 Lahnau, D-35633 Germany
//    317;MKS Nordhausen;Maik John, Owner;Bielener Straße 21 Nordhausen, D-99734  Germany
//    318;De Gier Drive Systems B.V.;Gerard A. Buter, Manager R&D;Westlandseweg 9 PG Wateringen, 2291 Netherlands
//    319;Cypress Envirosystems;Ramesh Songukrishnasamy, VP/GM Building Solutions;198 Champion Court San Jose, CA 95134-1709 USA
//    320;SMARTron s.r.o.;Sergey Ziskovich, General Manager;10 Husovo nameti 468/16 Prague, 10400 Czech Republic
//    321;Verari Systems, Inc.;Jennifer H. Skjellum, Vice President, Software Solutions;110 12th Street North, Suite 0103 Birmingham, AL 35203-1537 USA
//    322;K-W Electronic Service, Inc.;Jeffrey D. Cox, Manager, Operations & Customer Service;750 McMurray Road Waterloo, ON N2V 2G5 Canada
//    323;ALFA-SMART Energy Management;Alex Kugel, Hardware Development Manager;Kibbutz Beit Alfa 10802 Israel
//    324;Telkonet, Inc.;Eva C. Wang, Director, Software Development;20374 Seneca Meadows Parkway Germantown, MD 20876 USA
//    325;Securiton GmbH;Horst Geiser, Product Development Manager;Von-Drais-Strasse 33 Achern, 0-77855 Germany
//    326;Cemtrex, Inc.;Saagar Govil, Sales & Marketing Manager;19 Engineers Lane Farmingdale, NY 11735 USA
//    327;Performance Technologies, Inc.;John J. Grana, Sr. VP and GM, Embedded Systems Group;205 Indigo Creek Drive Rochester, NY 14626 USA
//    328;Xtralis (Aust) Pty Ltd;John Minack, Development Engineer;4 North Drive, Virginia Park 236-262 East Boundary Rd. Bentleigh East, Victoria Australia
//    329;TROX GmbH;Friedrich Sikosek, Product Management Systems;Heinrich-Trox-Platz D-47504 Neukirchen-Vluyn Germany
//    330;Beijing Hysine Technology Co., Ltd;Xiaoyong Zhao, Manager;No.D-415A Zhenghaodasha Beiwa Rd Haidian District Beijing China
//    331;RCK Controls, Inc.;James F Ring, President;9303 Chesapeake Drive Suite A1 San Diego, CA 92123 USA
//    332;Distech Controls SAS;Maxime Ormancey, Quality Manager;ZA Les Andres Rue de Pre Magne 69126 Brindas France
//    333;Novar/Honeywell;John Weisenberger, Product Manager;6060 Rockside Woods Blvd. Suite 400 Cleveland,OH 44131 USA
//    334;The S4 Group, Inc.;Steve E. Jones, President;585- 24th Street Suite 106 Ogden, UT 84401 USA
//    335;Schneider Electric;Ronald H. Naismith, Sr. Principal Technical Specialist;1 High Street MS 7-2B North Andover, MA 01845 USA
//    336;LHA Systems;Christo Malan, Development Engineer;P.O. Box 1278 Die Boord - 7613 South Africa
//    337;GHM engineering Group, Inc.;Gordon Maretzki, President;2303 Stillmeadow Road Oakville, Ontario L6M 4C7 Canada
//    338;Cllimalux S.A.;Paul Meyer, Technical Manager;Rue de l'lndustrie L-3895 FOETZ Luxembourg
//    339;VAISALA Oyj;Matti Kokki, Chief Engineer;Vanha Nurmijarventie 21 FI-01670 Vantaa Finland
//    340;COMPLEX (Beijing) Technology, Co., LTD.;Sally Li, Sales Manager;Room A503 HAOJING Building NO. 108 ZHICHUN Rd. HAIDIAN District, Beijing 100086 China
//    341;SCADAmetrics;Jim Mimlitz, President;1133 Pond Road Wildwood, MO 63038 USA
//    342;POWERPEG NSI Limited;Ringo Lee, Director;Unit C - 18/F Tung Chiu Commercial Center 193 Lockhart Road Wanchai, Hong Kong
//    343;BACnet Interoperability Testing Services, Inc.;Edward Hague, President;809 B Cuesta Drive Suite 2180 Mountain View, CA 94040-3667 USA
//    344;Teco a.s.;Jaromir Klaban, Member of the Board;Havlickova 260 Kolin 4 - 280 58 Czech Republic
//    345;Plexus Technology, Inc.;James Leaper, CEO;5451 San Milano Avenue Las Vegas, NV 89141 USA
//    346;Energy Focus, Inc.;Keith Kazenski, Electrical Engineer;32000 Aurora Road Solon, OH 44139 USA
//    347;Powersmiths International Corp.;Philip J. A. Ling, Vice President - Technology;10 Devon Road Brampton, Ontario L6T 5B5 Canada
//    348;Nichibei Co., Ltd.;Akira Nakashima, Director of Product Development;4061-6 Nakatsu Aikawa-machi, Aikou-gun Kanagawa, 243-0303 Japan
//    349;HKC Technology Ltd.;Martin Ip, Project Director;25/F., Oxford House, Taikoo Place 979 King's Road Quarry Bay, Hong Kong China
//    350;Ovation Networks, Inc.;John Schnipkoweit, Chief Technology Officer;222 Third Ave SE Suite 276 Cedar Rapids, IA 52401 USA
//    351;Setra Systems;Coleman Brumley, Software Engineering Manager;159 Swanson Road Boxborough, MA 01719-1304 USA
//    352;AVG Automation;Raj Tiwari, Vice President & CTO;4140 Utica Ridge Road Beltendorf, IA 52722 USA
//    353;ZXC Ltd.;David Barrett, Director;Level 2, 6 Clayton Street Newmarket Auckland, 1023 New Zealand
//    354;Byte Sphere;Nicholas Saparoff, President;955 Massachusetts Avenue Suite 141 Cambridge, MA 02139 USA
//    355;Generiton Co., Ltd.;Jefferson Liu, General Manager;8F-1, No. 81, Shuili Road Hsinchu City, Taiwan China
//    356;Holter Regelarmaturen GmbH & Co. KG;Ralf Schulze, Managing Director;Helleforthstraße 58 - 60 33758 Schloß Holte-Stukenbrock Germany
//    357;Bedford Instruments, LLC;Adam Smola, Manager;11 Cajun Court Bedford, NH 03110-5304 USA
//    358;Standair Inc.;Pierre Longval, Vice President;3311 Industrial Blvd. Laval, Quebec, H7L 4S3 Canada
//    359;WEG Automation - R&D;Alexandre José da Silva, Software Design;Av. Prefeito Waldemar Grubba 3000 - Jaragua do Sul SC, 89256-900 Brazil
//    360;Prolon Control Systems ApS;Thomas Maltesen, Managing Director;Herstedvesterstraede 56 DK-2620 Albertslund Denmark
//    361;Inneasoft;Sebastien Cand, General Manager;198 Avenue de la resistance Cedex 237 38920 Crolles France
//    362;ConneXSoft GmbH;Peter Vontluck, Project Manager;Oedenpullach Haus 1 D-82041 Oberhaching Germany
//    363;CEAG Notlichtsysteme GmbH;Jurgen Prasuhn, Technical Director, Cooper Safety;Senator-Schwartz-Ring 26 59494 Soest Germany
//    364;Distech Controls Inc.;Charles Gauvin, Vice President - R&D;4005-B, Matte Blvd Brossard (QC), J4Y 2P4 Canada
//    365;Industrial Technology Research Institute;Min-Hsien Chien, Engineer;195, Sec 4, Chung Hsing Road Chutung, Hsinchu City, Taiwan China
//    366;ICONICS, Inc.;David M. Oravetz, VP of Engineering;100 Foxborough Blvd. Foxboro, MA 02035 USA
//    367;IQ Controls s.c.;Piotr Fedyk, Director;UL. Tunkla 94 Ruda Slaska, 41-707 Poland
//    368;OJ Electronics A/S;Jens Antonsen, Product Manager HVAC;Stenager 13 B DK-6400 Sonderborg, Denmark
//    369;Rolbit Ltd.;Zeev I Horovitz, Director R&D;21 Ha'shoam St. P.O. Box 38 Barkan Industrial Area, 44820 Israel
//    370;Synapsys Solutions Ltd.;Andy Devine, Technical Director;1 Woodlands Court - Albert Drive Burgess Hill West Sussex, RH159TN United Kingdom
//    371;ACME Engineering Prod. Ltd.;Leon Karanfil, Product Manager;5706 Royalmount Ave Montreal (Quebec), H4P 1K5 Canada
//    372;Zener Electric Pty, Ltd.;Ron Jackson, Managing Director;366 Horsley Road Milperra NSW 2214, Australia
//    373;Selectronix, Inc.;Jerauld T. Numata, President;16419 199th Court Northeast Woodinville, WA 98077-5401 USA
//    374;Gorbet & Banerjee, LLC.;Matt Gorbet, Managing Partner;54 Afton Avenue Toronto, ON M6J 1S1 Canada
//    375;IME;Ildebrando Vignati, R & S Manager;Via Travaglia 7 20094 Corsico (MI) Italy
//    376;Stephen H. Dawson Computer Service;Stephen Dawson;P.O. Box 30391 Knoxville, TN 37930-0391 USA
//    377;Accutrol, LLC;Fred George;600 Pepper Street Monroe, CT 06468 USA
//    378;Schneider Elektronik GmbH;Bernd Drost;Industriestrasse 4 61449 Steinbach Germany
//    379;Alpha-Inno Tec GmbH;Jürgen Eschenbacher;Industriestrasse 3  D-95359 Kasendorf Germany
//    380;ADMMicro, Inc.;Crystal DePuy;2797 Frontage Road NW Suite 1000 Roanoke,VA 24017 USA
//    381;Greystone Energy Systems, Inc.;Lucas Steeves;150 English Drive Moncton, NB E1E 4G7 Canada
//    382;CAP Technologie;Patrice Chateau;13 Rue Sebastien Letourneux 44450 St Julien de Concelles France
//    383;KeRo Systems;Kenn Roland;Allikevej 3  DK-2970 Hoersholm Denmark
//    384;Domat Control System s.r.o.;Jan Vidim;U Panasonicu 376 CZ-530 06 Pardubice Czech Republic
//    385;Efektronics Pty. Ltd.;Philip Scott;1/9 Price Street, Nerang P.O. Box 307, Nerang Old 4211 Australia
//    386;Hekatron Vertriebs GmbH;Christian Wenzl;Brühlmatten 9 79295 Sulzburg Germany
//    387;Securiton AG;Christian Uehlinger;Alpenstrasse 20 CH - 3052 Zollikofen Switzerland
//    388;Carlo Gavazzi Controls SpA;Fabio Facchin;Via Safforze 8  I-32100 Belluno Italy
//    389;Chipkin Automation Systems;Peter Chipkin;3381 Cambie St, #211  Vancouver, BC V5Z 4R3 Canada
//    390;Savant Systems, LLC;Bruce Myers;770 Main Street Osterville, MA 02655 USA
//    391;Simmtronic Lighting Controls;Ian Moir-Porteous;Waterside Charlton Mead Lane Hoddesdon Hertfordshire, EN11 0QR United Kingdom
//    392;Abelko Innovation AB;Anders Lindgren;Box 808 - Industrivagen 17 SE-971 - 25 Lulea Sweden
//    393;Seresco Technologies Inc.;David Lucas;1283 Algoma Road, Unit 1 Ottawa, Ontario, K1B 3W7 Canada
//    394;IT Watchdogs;Charlie Mayne;12885 Research Blvd., Suite 203 Austin, TX 78750 USA
//    395;Automation Assist Japan Corp.;Yukiko Futatsugi;8F Aios Akihabara Bldg. 3-2-2 Ueno, Taitou-Ku 110-0005 - Tokyo Japan
//    396;Thermokon Sensortechnik GmbH;Jorg Teichmann;Aarstrasse 6 35756 Mittenaar Germany
//    397;EGauge Systems, LLC;David Mosberger;1935 Stony Hill Road Boulder, CO 80305 USA
//    398;Quantum Automation (ASIA) PTE, Ltd.;Roberto De Jesus;18 Kaki Bukit Road 3 #01-01/02/03 Entrepreneur Business Center Singapore 415978 Singapore
//    399;Toshiba Lighting & Technology Corp.;Yasushi Morimoto;1-201-1 Funakoshi-cho Yokosuka, 237-8510 Japan
//    400;SPIN Engenharia de Automação Ltda.;Luis Closs;SCLN-212 Bloco D - Sala 101 CEP 70864-540 Brasilia, DF Brazil
//    401;Logistics Systems & Software Services India PVT. Ltd.;Ajay Nirantar;136, Solaris - 1,B - Wing, 1st Floor Opp. L&T Gate No.6, Saki-Vihar Rd. Powai, Andheri (E), Mumbai 400 072 India
//    402;Delta Controls Integration Products;Carl Neilson;61 Seagirt Road East Sooke - BC, V9Z 1A3 Canada
//    403;Focus Media;Andrey Tsylev;124498, Russian Federation, Moscow Zelenograd, MIET, bid. 5 str. 9, # 9209, Russia
//    404;LUMEnergi Inc.;Timothy Parry;8371 Central Ave - Unit B Newark, CA 94560 USA
//    405;Kara Systems;Mr. Emanuel Galea;Ahornstrasse 12 90513 Zirndorf Germany
//    406;RF Code, Inc.;Martin Stich;9229 Waterford Centre Blvd. Suite 500 Austin, TX 78758 USA
//    407;Fatek Automation Corp.;Raymond Hong, President;26 Fl., No. 29, Sec. 2, JungJeng E. Rd. Danshuei Jen, Taipei Taiwan, 251 China
//    408;JANDA Software Company, LLC;Coleman Brumley, President;206 Alexander Avenue Greensburg, PA 15601 USA
//    409;Open System Solutions Limited;Steve Brown;Unit 33, Mitchell Point, Ensign Way Hamble, Southampton, S031 4RF United Kingdom
//    410;Intelec Systems PTY Ltd.;Shane Carlyon;Unit 6,61-63 Steel Street Capalaba, QLD, 4159 Australia
//    411;Ecolodgix, LLC;Woody Boyd;1251 Jupiter Park Drive, Suite 10 Jupiter, FL 33458 USA
//    412;Douglas Lighting Controls;George Capelo;4455 Juneau St. Burnaby, B.C. V5C 4C4 Canada
//    413;iSAtech GmbH;André Kraski;Alt-Moabit 59-61 DE-10555 Berlin, Germany
//    414;AREAL;Julien Bennet;41 Rue Principale 57450 Theding France
//    415;Beckhoff Automation;Ralf Vienken;Huelshorstweg 20 33415 Verl Germany
//    416;IPAS GmbH;Olaf Russak;Grabenstrasse 149a D-47047 Duisburg, Germany
//    417;KE2 Therm Solutions;Steve Roberts;1874 Hwy A Suite 210 Washington, MO 63090 USA
//    418;Base2Products;John Novak;Post Office Box 41 Montgomeryville, PA 18936 USA
//    419;DTL Controls, LLC;Sue Sackett;7171 Mercy Road-Suite 600 Omaha, NE 68106 USA
//    420;INNCOM International, Inc.;Philipp Roosli;277 West Main Street Niantic, CT 06357 USA
//    421;BTR Netcom GmbH;Franz Albicker;Im Tal 2  78176 Blumberg Germany
//    422;Greentrol Automation, Inc;Chris Mezenski;156 Holly View Lane Loris, SC 29569 USA
//    423;BELIMO Automation AG;Peter Schmidlin;Brunnenbachstrasse 1 - CH-8340 Hinwil Switzerland
//    424;Samsung Heavy Industries Co, Ltd;Tae-Kyung Chung;493 Banweol-Dong, Hwaseong-City Gyeonggi-Do, 445-330 Korea
//    425;Triacta Power Technologies, Inc.;David Perry;Box 582, 7 Mill Street Almonte, ON KOA 1AO Canada
//    426;Globestar Systems;Jason Wilson;7 Kodiak Crescent Suite 100 Toronto, Ontario, M3J 3E5 Canada
//    427;MLB Advanced Media, LP;Joseph Inzerillo;75 Ninth Avenue 5th Floor New York, NY 10011 USA
//    428;SWG Stuckmann Wirtschaftliche Gebäudesysteme GmbH;Sascha Stuckmann;Hauptstr. 68 59269 Beckum Germany
//    429;SensorSwitch;Stacy Meszaros;900 Northrop Road Wallingford, CT 06492 USA
//    430;Multitek Power Limited;Chris Scanlan;Lancaster Way - Earls Colne Business Park Earls Colne Colchester, Essex C06 2NS United Kingdom
//    431;Aquametro AG;Andreas Lestin;Ringstrasse 75 CH-4106 Therwil Switzerland
//    432;LG Electronics Inc.;Haejin Kim;Changwon 2nd Plant 76, Seongsan-dong Changwon City Gyeongnam 641-713 Korea
//    433;Electronic Theatre Controls, Inc.;Troy Hatley;3031 Pleasant View Road P.O. Box 620979 Middleton, WI 53562-0979 USA
//    434;Mitsubishi Electric Corporation Nagoya Works;Yoshihiro Nagataki;1-14, Yada-minami 5-Chome, Higashi-ku Nagoya 461-8670, Japan
//    435;Delta Electronics, Inc.;Tai-Tsung Huang;39 Section 2 Huandong Road, Shanhua Township Tainan Country, Taiwan 74144 China
//    436;Elma Kurtalj, Ltd.;Ninoslav Kurtalj;Viteziceva 1a 1000 Zagreb Croatia
//    437;Tyco Fire & Security GmbH;Faruk Meah, Senior Manager R&D;The Summit Security House Hanworth Road Sunbury-on-Thames, Middlesex TW16 5DB United Kingdom
//    438;Nedap Security Management;Albert Dercksen;Parallelweg 2 7141 DC Groenlo - P.O. Box 6 7140 AA Groenlo Netherlands
//    439;ESC Automation Inc.;Jim Spronsen;17850 56th Avenue Surrey, BC V3S 1C7 Canada
//    440;DSP4YOU Ltd.;Antoine Rouget;17 Hung To Road, Unit 305 Kwun Tong, Hong Kong
//    441;GE Sensing and Inspection Technologies;Christopher Frail;1100 Technology Park Drive Billerica, MA 01821 USA
//    442;Embedded Systems SIA;Edgars Klavinskis;Katolu str. 47 Riga, LV-1003 Latvia
//    443;BEFEGA GmbH;Manfred Kraußer;Reichenbacher Straße 22 0-91126, Schwabach Germany
//    444;Baseline Inc.;Tim Hickenlooper;2700 East Lanark Street Suite 100 Meridian, ID 83642 USA
//    445;Key2Act;Dante Gabrielli;1970 South Calhoun Road New Berlin, WI 53151 USA
//    446;OEMCtrl;Michael Wilson;1025 Cobb Place Boulevard Marietta, GA 30144 USA
//    447;Clarkson Controls Limited;Glenn Smith;3 Mill Pool - Nash Lane Belbroughton Worcestershire, DY9 9AF United Kingdom
//    448;Rogerwell Control System Limited;Roger Song;Room 1401, Cambridge house 26-28 Cameron Road Tsimshatsui, Kowloon, Hong Kong
//    449;SCL Elements;Hami Chanon;5800 St-Denis, # 222 Montreal, Quebec H2S3L5 Canada
//    450;Hitachi Ltd.;Koichi Koyano, Senior Engineer;1 Horiyamasita, Hadano-shi Data Ctr. Energy Saving Facilities Systems Dept. Kanagawa-ken, 259-1392 Japan
//    451;Newron System SA;Serge Le Men, Manager-Board Member;33 rue Paul Gauguin 31100 Toulouse France
//    452;BEVECO Gebouwautomatisering BV;L.C. Segaar;Maseratilaan 8 3261 NA Oud-Beijerland Netherlands
//    453;Streamside Solutions;William Russell;P.O. Box 1576 Estes Park, CO 80517 USA
//    454;Yellowstone Soft;Hermann Betz;Brunnenstrasse 32 89584 Ehingen, Germany
//    455;Oztech Intelligent Systems Pty Ltd.;Bob Gulliver;146 Pittwater Road Gladesville, NSW 2111 Australia
//    456;Novelan GmbH;Stefan Reis;Bahnhofstrasse 2 0-95359 Kasendorf, Germany
//    457;Flexim Americas Corporation;Peter Chirivas;250-V Executive Drive Edgewood,NY 11717 USA
//    458;ICP DAS Co., Ltd.;Anold Chao;3 FI. No. 505, Jhongshan 2nd Rd. Qianjin District Kaohsiung City 80146 Taiwan China
//    459;CARMA Industries Inc.;Rick Williams;132 Walsh Road Lindsay, ON K9V 4R3 Canada
//    460;Log-One Ltd.;Hugues de Milleville;8521 Sideroad 10 RR1 Loretto, ON LOG 1 LO Canada
//    461;TECO Electric & Machinery Co., Ltd.;Steven Yang;2F, No. 22. Sec 1, Chung Shan Rd. Sin-Chuang City Taipei County 242, Taiwan China
//    462;ConnectEx, Inc.;Karl Kaiser;1153 Sesame Drive Sunnyvale, CA 94087 USA
//    463;Turbo DDC Südwest;Roland Quenzer;Hauptstrasse 53  D-66957 Trulben Germany
//    464;Quatrosense Environmental Ltd.;Xiangyang Zheng;5935 Ottawa Street  P.O. Box 749 Richmond, Ontario K0A 2Z0 Canada
//    465;Fifth Light Technology Ltd.;Sashi Bala;1155 North Service Road W Unit # 7 Oakville, Ontario L6M 3E3 Canada
//    466;Scientific Solutions, Ltd.;Randall Logan;4972 Canium Road 108 Mile Ranch, BC V0K 2Z0  Canada
//    467;Controller Area Network Solutions (M) Sdn Bhd;K.H. Tang;34-2, Jalan Puteri 2!2 Bandar Puteri Puchong 47100 Puchong Selangor Malaysia
//    468;RESOL - Elektronische Regelungen GmbH;Sascha Kunze;Heiskampstraße 10 45527 Hattingen Germany
//    469;RPBUS LLC;Paul Hennington;11407 Centennial Trail Austin, TX 78726 USA
//    470;BRS Sistemas Eletronicos;Marcelo Richter da Silva;Rua Gomes de Freitas 491/204 Porto Alegre, RS 91380-000 Brazil
//    471;WindowMaster A/S;Jesper Darum;Skelestedet 13 DK-2950, Vedbaek Denmark
//    472;Sunlux Technologies Ltd.;Sanjeev Kulkarni;#99 Jakkasandra Extn., Jakkasandra Industrial Area Off Sarjapur Road Koramangala, Bangalore 560 034 India
//    473;Measurlogic;John Stratford;7334 S. Alton Way Suite 14M Centennial, CO 80112 USA
//    474;Frimat GmbH;Johann Frey;Dr.-von-Rieppel-Straße 2 92637 Weiden Germany
//    475;Spirax Sarco;Alun Barnett;1150 Northpoint Blvd. Blythewood, SC 29016 USA
//    476;Luxtron;Davide Fabri;via Valle Po, 82 12100, Cuneo Italy
//    477;Raypak Inc;Ray Hallit;2151 Eastman Ave Oxnard, CA 93030 USA
//    478;Air Monitor Corporation;Dean Debaun;1050 Hopper Ave Santa Rosa, CA 94503 USA
//    479;Regler Och Webbteknik Sverige (ROWS);Robin Andersson;Sondrumsvagen 12C 302 39 HALMSTAD, Sweden
//    480;Intelligent Lighting Controls Inc.;Tom Biehl;5229 Edina Industrial Blvd Minneapolis, MN 55439 USA
//    481;Sanyo Electric Industry Co., Ltd;Kouji Mizusawa;1-59-11 Umegaoka, Setagaya-ku Tokyo, 154-0022 Japan
//    482;E-Mon Energy Monitoring Products;Kantol Khek;850 Town Center Drive Langhorne, PA 19047 USA
//    483;Digital Control Systems;Don Allen;7401 SW Capitol Highway Portland, OR 97219 USA
//    484;ATI Airtest Technologies, Inc.;Harry Skibbe;#9-1520 Cliveden Ave Delta, BC V3M 6J8 Canada
//    485;SCS SA;Gabriele Cavadini;Via Pini, 32 Biasca, Switzerland
//    486;HMS Industrial Networks AB;Joakim Wiberg;PO Box 4126 Halmstad, 300 04 Sweden
//    487;Shenzhen Universal Intellisys Co Ltd;Jack Tan Li Jun, Chief Executive Officer;Rm 1608, Star Building, New Xinsha Rd Shajing Town , Baoan District Shenzhen, China
//    488;EK Intellisys Sdn Bhd;Yin Kean Meng;16-3 Jalan Puteri 2/6 Bandar Puteri Puchong, 47100 Malaysia
//    489;SysCom;Ulrich Pink;Im Berggarten 3 Bad Marienberg, 56470 Germany
//    490;Firecom, Inc.;Patrick Adamo;41-10 Mercedes Way Edgewood, NY 11717 USA
//    491;ESA Elektroschaltanlagen Grimma GmbH;Thomas Steuernagel;Broner Ring 30 Grimma, 04668 Germany
//    492;Kumahira Co Ltd;Makoto Fujinawa;4-34 Ujina Higashi 2-Chome Minamiku, Hiroshima 734-8567 Japan
//    493;Hotraco;Ing. T. Driessen;PO Box 6086 AB Horst, 5960 Netherlands
//    494;SABO Elektronik GmbH;Ernst-Friedrich Althoff;Lohbachstr. 14 Schwerte, 58239 Germany
//    495;Equip'Trans;Thierry Dubosse;31 rue Paul Cezanne La Rochette, 77000 France
//    496;Temperature Control Specialities Co., Inc (TCS);Josef Pelc, Engineering Director;2800 Laura Ln Middleton, WI 53562 USA
//    497;FlowCon International A/S;Bjarne Ibsen;Trafikcenter Alle 17 Slagelse, DK-4200 Denmark
//    498;ThyssenKrupp Elevator Americas;Beom Ko;9280 Crestwyn Hills Drive Memphis, TN 38125 USA
//    499;Abatement Technologies;Gary Kruse;605 Satellite Blvd Ste 300 Suwanee, GA 30024 USA
//    500;Continental Control Systems, LLC;Nathaniel Crutcher;3131 Indian Rd Ste A Boulder, CO 80301 USA
//    501;WISAG Automatisierungstechnik GmbH & Co KG;Michael Calmbach;Ohmweg 11-15 Mannheim, 68199 Germany
//    502;EasyIO;Lim Chiat;No 32-2 & 32-3, Jalan Puteri 2/4 Bandar Puteri, 47100 Malaysia
//    503;EAP-Electric GmbH;Andrea Harrer;Florianistraße 4 Bruck/Leitha, 2460 Austria
//    504;Hardmeier;Illimar Soot;Panu mnt 102 Tallinn, 11312 Estonia
//    505;Mircom Group of Companies;Jason Falbo;25 Interchange Way Toronto, ON L4K 5W3 Canada
//    506;Quest Controls;Ken Nickel;208 9th Street Dr West Palmetto, FL 34221 USA
//    507;Mestek, Inc;Mark Rawson;260 North Elm St Westfield, MA 01085 USA
//    508;Pulse Energy;Chuck Clark;576 Seymour St Ste 600 Vancouver, BC V6B 3K1 Canada
//    509;Tachikawa Corporation;Masato Kuwana;3-1-12 Mita, Minato-Ku Tokyo, 108-8334 Japan
//    510;University of Nebraska-Lincoln;Stefan Newbold;942 North 22nd Street Lincoln, NE 68588 USA
//    511;Redwood Systems;Robert Henig;3839 Spinnaker Court Fremont, CA 94538 USA
//    512;PASStec Industrie-Elektronik GmbH;Andreas Penzel;Unter den Weiden 31 Crimmitschau, 08451 Germany
//    513;NgEK, Inc.;George Brunemann;3342 Parkhill Dr Cincinnati, OH 45248 USA
//    514;t-mac Technologies;Chris Childs;Stand Park Sheffield Road Chesterfield, Derbyshire S41 8JT United Kingdom
//    515;Jireh Energy Tech Co., Ltd.;Jaehwan Joo;325-2, Seongsu-dong 2(i) Seongdong-gu Seoul Korea
//    516;Enlighted Inc.;Shailendra Karody;1451 Grant Road Ste 200 Mountain View, CA 94040 USA
//    517;El-Piast Sp. Z o.o;Ryszard Dworzecki;u. J. Pitsudskiego 74 Wroclow, 50-020 Poland
//    518;NetxAutomation Software GmbH;Pawel Furtak;Maria Theresiastr. 41 Top 10 Wels, 4600 Austria
//    519;Invertek Drives;David Jones;Offas Dyke Business Park Welshpool Powys, SY15 6LL United Kingdom
//    520;Deutschmann Automation GmbH & Co. KG;Gunther Lawaczeck;65520 Bad Camberg Carl-Zeiss-Str. 8 Bad Camberg, 65520 Germany
//    521;EMU Electronic AG;Severin Koller;Jochlerweg 4 Baar, CH 6340 Switzerland
//    522;Phaedrus Limited;Roy Schofield;The Boiler House, 5 Marsh House Mill Brussels Road Darwen, BB3 3JJ United Kingdom
//    523;Sigmatek GmbH & Co KG;Hans-Roman Seifert;Sigmatekstrasse 1 Lamprechtshausen 5112 Austria
//    524;Marlin Controls;Ken Chigani;PO Box 550457 Dallas, TX 75355 USA
//    525;Circutor, SA;Bernat Garcia;Vial Sant Jordi, sn Viladecavalls, 08232 Spain
//    526;UTC Fire & Security;Stipan Ruzicka;Level 1, 271 Wellington Rd Mulgrave, VIC 3170 Australia
//    527;DENT Instruments, Inc.;Arne LaVen;925 SW Emkay Drive Bend, OR 97702 USA
//    528;FHP Manufacturing Company - Bosch Group;Adam Sterne;601 NW 65th Court Ft. Lauderdale, FL 33309 USA
//    529;GE Intelligent Platforms;John Chisholm;325 Foxboro Blvd Foxboro, MA 02035 USA
//    530;Inner Range Pty Ltd;Alf Katz;1 Millennium Court Knoxfield, Victoria 3180 Australia
//    531;GLAS Energy Technology;Peter Mc Shane;Johnstown Business Centre, Johnstown House Johnstown, Naas, County Kildare Ireland
//    532;MSR-Electronic-GmbH;Johann Schuetzeneder;Würdingerstr. 27 Pocking, D-94060 Germany
//    533;Energy Control Systems, Inc.;Conrad Stucki;2940-A Cole Court Norcross, GA 30071 USA
//    534;EMT Controls;A. Vardar;Boran Plaza 1376 Sk.3-AN Halkapinar Izmir, 35170 Turkey
//    535;Daintree Networks Inc.;Peter Cobb;1503 Grant Road Ste 202 Mountain View, CA 94040 USA
//    536;EURO ICC d.o.o;Nikola Jelic;Trscanska 21 Belgrade, 11808 Serbia
//    537;TE Connectivity Energy;Phil Houghton;Freebournes Road Witham, Essex CM8 3AH United Kingdom
//    538;GEZE GmbH;Alexander Landgraf;Reinhold-Voester-Str. 21-29 Leonberg, 71229 Germany
    GEZE_GMBH(538, "GEZE GmbH"),
//    539;NEC Corporation;Kiminori Tojima;Mita Kokusai Building Annex 1-4-28 Mita Minato-ku, Tokyo 108-0073 Japan
    NEC_CORPORATION(539, "NEC Corporation");
//    540;Ho Cheung International Company Limited;Thomas Kwok;Flat F, 6/F Imperial Building 58, Canton Road Tsimshatsui, Kowloon Hong Kong
//    541;Sharp Manufacturing Systems Corporation;Hirakazu Tsukamoto;4-1-33, Atobe-honmachi Yao-City, Osaka 581-8581 Japan
//    542;DOT CONTROLS a.s.;Zdenek Hanak, Jr.;Velehradska 593 Stare Mesto, 686 03 Czech Republic
//    543;BeaconMedæs;Ray Wilson;1800 Overview Dr Rock Hill, SC 29730 USA
//    544;Midea Commercial Aircon;Jie Yan;Western of No 3 Industrial District Beijiao, Shunde Foshan, Guangdong 528311 China
//    545;WattMaster Controls;Michael Bell;8500 NW River Park Dr. Parkville, MO 64152 USA
//    546;Kamstrup A/S;Bjarne Jensen;Industrivej 28, Stilling Skanderborg, DK-8660 Denmark
//    547;CA Computer Automation GmbH;Ernst Mack;Tscheulinstrasse 21 Teningen, D-79331 Germany
//    548;Laars Heating Systems Company;Chuck O'Donnell;20 Industrial Way Rochester, NH 03867 USA
//    549;Hitachi Systems, Ltd.;Toshihito Kon;1-2-2 Osaki, Sshinagawa-ku Tokyo, 141-0032 Japan
//    550;Fushan AKE Electronic Engineering Co., Ltd.;Pan Qing;3F, District C, Hantian Technology Park No. 17 Shenhai Road Nanhai, Fushan Guangdong 528200 China
//    551;Toshiba International Corporation;Terry Voigt;13131 West Little York Rd Houston, TX 77041 USA
//    552;Starman Systems, LLC;Richard Treffers;10 Vista Lane Alamo, CA 94507 USA
//    553;Samsung Techwin Co., Ltd.;HoSung Yoo;28, Seongju-dong, Seongsan-gu Changwon-si Gyseongsangnam-do Korea
//    554;ISAS-Integrated Switchgear and Systems P/L;Tony Pearce;494 Stuart Highway Winnellie, N.T. 0820 Australia
//    555;Reserved for ASHRAE;;
//    556;Obvius;Stephen Herzog;3300 NW 211th Terrace Hillsboro, OR 97124 USA
//    557;Marek Guzik;Marek Guzik;Van Deursenstraat 18 2671 EP Naaldwijk Netherlands
//    558;Vortek Instruments, LLC;Vince Cisar;8475 W I-25 Frontage Road Ste 300 Longmont, CO 80504 USA
//    559;Universal Lighting Technologies;Travis Berry;1430 Wall Triana Hwy Madison, AL 35756 USA
//    560;Myers Power Products, Inc.;Bruce Steigerwald;44 South Commerce Way Bethlehem, PA 18017 USA
//    561;Vector Controls GmbH;Rolf Schweizer;Sumpfstrasse 3  6300 Zug Switzerland
//    562;Crestron Electronics, Inc.;Toine Leerentveld;22 Link Drive Rockleigh, NJ 07647 USA
//    563;A&E Controls Limited;Fraser Eadie;7 Grove Street Musselburgh East Lothian, Scotland EH21 7EZ United Kingdom
//    564;Projektomontaza A.D.;Dejan Medan;Poenkareova 20 Belgrade Republic of Serbia Serbia
//    565;Freeaire Refrigeration;Richard Travers;151-8 Mad River Canoe Road Waitsfield, VT 05673 USA
//    566;Aqua Cooler Pty Limited;Andrew Blackmore;161 Orchard Rd Chester Hill, NSW 2162 Sydney Australia
//    567;Basic Controls;Lori Lee;No 803, C3 Zone, Innovation Mansion No 182 Science Ave Guangzhou, Guangdong China
//    568;GE Measurement and Control Solutions Advanced Sensors;Norman Hannotte;6860 Cortona Dr Ste B Goleta, CA 93117 USA
//    569;EQUAL Networks;Dale Loia;453 Ravendale Dr Ste E Mountain View, CA 94043 USA
//    570;Millennial Net;David Hirst;285 Billerica Road Chelmsford, MA 01824 USA
//    571;APLI Ltd;Peter Ratkos;Kladnianska 1 821 05 Bratislava Slovakia
//    572;Electro Industries/GaugeTech;Erran Kagan;1800 Shames Drive Westbury, NY 11590 USA
//    573;SangMyung University;Jeong Uk Kim;Dept of Energy Grid Jongno-gu, Seoul Hongjimun 2 Way 20, 101-743 Korea
//    574;Coppertree Analytics, Inc.;Ken Lockhart;100-5265-185A St Surrey, BC V35 7A4 Canada
//    575;CoreNetiX GmbH;Andreas Krause;Gustav-Meyer-Allee 25 Berlin, D-13355 Germany
//    576;Acutherm;Robert Kline;1766 Sabre Street Hayward, CA 94545 USA
//    577;Dr. Riedel Automatisierungstechnik GmbH;Jochen Jagers;Greifswalder Str. 4 DE-10405, Berlin Germany
//    578;Shina System Co., Ltd;Man-Seung Yoo;6FL-601, The-O Valley Anyang International Distribute Complex Hogye 1 Dong, Dongan-Gu 431-763 Korea
//    579;Iqapertus;Ireneusz Cicherski;ul. Sportowa 8 Gdynia, 81-300 Poland
//    580;PSE Technology;Patrick McCormick;1111 Jefferson Drive Bldg 4 Berthoud, CO 80513 USA
//    581;BA Systems;Jorn Albrektsen;Petershvilevej 1 Helsinge, DK-3200 Denmark
//    582;BTICINO;Fabio Mauri;Via Manara, 4 Erba, 22036 Italy
//    583;Monico, Inc.;Karen Taylor;5527 Louetta Rd Ste D Spring, TX 77379 USA
//    584;iCue;Ashutosh Vighne;3148 Levante St Carlsbad, CA 92009 USA
//    585;tekmar Control Systems Ltd.;Jason Nelson;5100 Silver Star Rd Vernon, B.C. V1B 3K4 Canada
//    586;Control Technology Corporation;Kevin Halloran;25 South Street Hopkinton, MA 01748 USA
//    587;GFAE GmbH;Rolf Eigenheer;Gennersbrunnerstrasse 71 Schaffhausen, CH 8207 Switzerland
//    588;BeKa Software GmbH;Bernhard Leisch;Tech Gate Vienna Donau-City-Str. 1 Vienna, A-1220 Austria
//    589;Isoil Industria SpA;Roberto Guazzoni;27, via F. lli Gracchi Cinisello, Balsamo 20092 Italy
//    590;Home Systems Consulting SpA;Ulderico Arcidiaco;Strada 4-Palazzo Q6 Milanofiori, 20089 Italy
//    591;Socomec;Thomas Bernard;1 rue de Westhouse BP: 60010 Benfeld Cedex, 67235 France
//    592;Everex Communications, Inc.;Richard Nidever;1045 Mission Ct. Fremont, CA 94539 USA
//    593;Ceiec Electric Technology;Simon Ki;8/F West Side, Bldg. 201  Terra Industrial & Tradepark Che Gong Miao, Guangdong 518040 China
//    594;Atrila GmbH;Andre Keller;Weinreben 1  PO Box 4412 Zug, 6304 Switzerland
//    595;WingTechs;Wyvern Wang;No1, Huoju Street High-Tech District Dalian City, 116023 China
//    596;Shenzhen Mek Intellisys Pte Ltd.;Mai Zhao;6C, Kechuang Mansion Quanzhi Technology Park, Shajing Shenzhen, Guangdong China
//    597;Nestfield Co., Ltd.;YuChul Kim;#104, Ansan IT Industry Agency 643-7 Wonkok-dong,  Danwon-gu Ansan-si, Gyeonggi-do 425-130 Korea
//    598;Swissphone Telecom AG;Adrian Baumgartner;Fälmisstrasse 21 CH-8833 Samstagern  Switzerland
//    599;PNTECH JSC;Nguyen Hoang Nam;352B/25 Phan Van Tri Street Ward 11 Binh Thanh District Ho Chi Minh City Vietnam
//    600;Horner APG, LLC;Chuck Ridgeway;59 South State Avenue Indianapolis, IN 46112 USA
//    601;PVI Industries, LLC;Giles Honeycutt;3209 Galvez Ave Ft. Worth, TX 76111 USA
//    602;Ela-compil;Jakub Bartkowiak;ul. Stoneczna 15A Poznan, 60-286 Poland
//    603;Pegasus Automation International LLC;David John, International Sales Manager;3904 Central Avenue Cheyenne, WY 82001 USA
//    604;Wight Electronic Services Ltd.;Mark Bunting;1c Golden Hill Park  Freshwater Isle of Wight, PO40 9UJ United Kingdom
//    605;Marcom;Luca Marani;Via Mezzacampagna 52 (int 29) Verona, 37135 Italy
//    606;Exhausto A/S;Klavs Kamuk;Odensevej 76, Langeskov, DK-5550 Denmark
//    607;Dwyer Instruments, Inc.;Mark Fisher;PO Box 373 Michigan City, IN 46361-0373 USA
//    608;Link GmbH;Markus Link;Bahnhofsallee 59-61 Butzbach, 35510 Germany
//    609;Oppermann Regelgerate GmbH;Dierk Astfalk;lm Spitzhau 1 Leinfelden, Echterdingen 70771 Germany
//    610;NuAire, Inc.;William Peters;2100 Fernbrook Lane Plymouth, MN 55447-4722 USA
//    611;Nortec Humidity, Inc.;Frank Pan;2740 Fenton Road Ottawa, ON K1T 317 Canada
//    612;Bigwood Systems, Inc.;Jeremy Keen;35 Thornwood Drive, Suite 400 Ithaca, NY 14850 USA
//    613;Enbala Power Networks;Todd Sankey;#211-930 W. 1st St North Vancouver, BC V7P 3N4 Canada
//    614;Inter Energy Co., Ltd.;Hirobumi Watanabe;3F Shin-Yokohama Bosei 3-20-12 Shin-Yokohama, Kohku-ku Yokohama City, Kanagawa pref 222-0033 Japan
//    615;ETC;Darren Legge;25 North Street Lewes East Sussex, BN7 2PE United Kingdom
//    616;COMELEC S.A.R.L;Jean-Marc Moscati;Chemin Departemental 908 Belcodene, 13720 France
//    617;Pythia Technologies;Jim Potter;175 S. Sandusky Street Ste 321 Delaware, OH 43015 USA
//    618;TrendPoint Systems, Inc.;Jonathan Trout;1595 East 6th Street Corona, CA 92879 USA
//    619;AWEX;Rafal Stanuch;Maslomiaca ul. Dluga 39 Michalowice, 32-091 Poland
//    620;Eurevia;David Loyer;ZI ATHELIA I  300, rue des Mattes 13600 La Ciotat France
//    621;Kongsberg E-lon AS;Ingar Pedersen;Dyrmyrgata 35 Kongsberg, N3611 Norway
//    622;FlaktWoods;Jan Risen;Flaktgatan 1 Jonkoping, Sweden
//    623;E + E Elektronik GES M.B.H.;Wolfgang Timelthaler;Langwiesen 7 Engerwitzdorf A4209 Austria
//    624;ARC Informatique;Benoit Lepeuple;2 Avenue de la Cristallerie Sevres, 92310 France
//    625;SKIDATA AG;Frank Lorenz;Untersbergstrasse 40 Groedig/Salzburg, A-5083 Austria
//    626;WSW Solutions;Stefan Ott;Erleinhofer Straße 14b Neunkirchen am Brand, 91077 Germany
//    627;Trefon Electronic GmbH;Stephan Trautvetter;Fichtenweg 6 Erfurt, D-99098 Germany
//    628;Dongseo System;Young Sik Kim;974-21 Mansu-1-dong Namdong-Gu-Incheon Korea
//    629;Kanontec Intelligence Technology Co., Ltd.;Liu Bing;Room 802, Bld 8, No. 901, Songfa Road Shanghai, China
//    630;EVCO S.p.A.;Stefano Feltrin;Via Feltre 81 Sedico, 32036 Italy
//    631;Accuenergy (Canada) Inc.;Liang Wang;2 Lansing Square Ste 1001 Toronto, ON M2J 4P8 Canada
//    632;SoftDEL;Chirag Nanavati;3rd Floor, Pentagon P4 Magarpatta City, Pune 411013 India
//    633;Orion Energy Systems, Inc.;Jason Young;2210 Woodland Drive Manitowoc, WI 54220 USA
//    634;Roboticsware;Kenji Saiki;1-11-1 Yoshino-cho Kita-ku Saitama, 331-0811 Japan
//    635;DOMIQ Sp. z o.o.;Filip Zawadiak;ul. Sarmacka 10B/19 02-972 Warszawa  Poland
//    636;Solidyne;Adem Erturk;4215 Kirchoff Road Rolling Meadows, IL 60008 USA
//    637;Elecsys Corporation;Dan Hughes;846 N. Mart-Way Court Olathe, KS 66061 USA
//    638;Conditionaire International Pty. Limited;Gary Clifford;12-20 Cook Road Marrickville NSW, 2214 Australia
//    639;Quebec, Inc.;Pierre Lefrancois;16 Place de Beaujeu Repentigny, QC J6A 3S9 Canada
//    640;Homerun Holdings;Dale McCarthy;3400 Copter Road Pensacola, FL 32514 USA
//    641;Murata Americas;Greg Ratzel, Manager, Software Engineering Group;4441 Sigma Road Dallas, TX 75244 USA
//    642;Comptek;Richard Gentry;37450 Enterprise Court Farmington Hills, MI 48331 USA
//    643;Westco Systems, Inc.;Brian Puck;7396 Lowell Boulevard Westminister, CO 80030 USA
//    644;Advancis Software & Services GmbH;Johanna Wunsch;Monzastr. 2 Langen, 63225 Germany
//    645;Intergrid, LLC;Robert Wills;PO Box 48  164 Hill Rd Temple, NH 03084 USA
//    646;Markerr Controls, Inc.;Gordon Maretzki;3858 Konkle Rd Beamsville, ON L0R 1B2 Canada
//    647;Toshiba Elevator and Building Systems Corporation;Hirashiki Yasuhiro;6-5-27, Kitashinagawa Shinagawa Ward Tokyo, Japan
//    648;Spectrum Controls, Inc.;Neal Meldrum;1704 132nd Ave, NE Bellevue, WA 98005 USA
//    649;Mkservice;Marek Kolodziejczyk;ul. Krowoderskich Zuchow 9/71 Krakow, PL 31-271 Poland
//    650;Fox Thermal Instruments;Rich Cada;339 Reservation Road Marina, CA USA
//    651;SyxthSense Ltd;Jukka Hurtta;3, Topsham Units Darf Business Park Topsham, Exeter EX3 0QH United Kingdom
//    652;DUHA System S R.O.;Petr Tomanek;633/2 Zelezna Street 619 00 Brno Czech Republic
//    653;NIBE;Andreas Johnsson;Hannabadsvagen 5 Box 14 Markaryd, SE-285-21 Sweden
//    654;Melink Corporation;Claire Hackman;5140 River Valley Road Milford, OH 45150 USA
//    655;Fritz-Haber-Institut;Heinz Junkes;Faradayweg 4-6 Berlin, 14195 Germany
//    656;MTU Onsite Energy GmbH, Gas Power Systems;Marcus Mücke;Dasinger Straße 11 Augsburg, 86165 Germany
//    657;Omega Engineering, Inc.;Daniel Jackson;PO Box 4047 Stamford, CT 06907 USA
//    658;Avelon;Leo Putz;Bandliweg 20 Zurich, CH-8048 Switzerland
//    659;Ywire Technologies, Inc.;Francis Beaudoin;6750 L'esplanade Suite 335 Montreal, QC H2V 4M1 Canada
//    660;M.R. Engineering Co., Ltd.;Chang Ro Yoon;Gonglk-Bldg #506  210 SeokChon-Dong Seoul, 138-845 Korea
//    661;Lochinvar, LLC;Mike Juhnke;300 Maddox Simpson Pkwy Lebanon, TN 37090 USA
//    662;Sontay Limited;Martin Schreiber;Four Elms Road Edenbridge Kent, TN8 6AB United Kingdom
//    663;GRUPA Slawomir Chelminski;Slawomir Chelminski;Mazurska 19/21 A 18 80-513 Gdansk, VAT 9570985581 Poland
//    664;Arch Meter Corporation;Debbie Teng;4F, No. 3-2, Industry E. Rd. 9, Science Park Hsinchu 300, Taiwan
//    665;Senva, Inc.;David Flinchbaugh;9290 SW Nimbus Ave Beaverton, OR 97008 USA
//    666;Reserved for ASHRAE;;
//    667;FM-Tec;Frederik Meerwaldt;Lerchenstr. 11 Westerham, 83620 Germany
//    668;Systems Specialists, Inc.;Mitch Randall;114 East Wright St Pensacola, FL 32501 USA
//    669;SenseAir;Joakim Enerud;Stationsgatan 12 Delsbo, SE-82060 Sweden
//    670;AB IndustrieTechnik Srl;Marc Barbuti;Via Julius Durst, 70 Bressanone (BZ), 39042 Italy
//    671;Cortland Research, LLC;Stephen McMahon;12 South Main Street Suite 207  P.O. Box 307 Homer, NY 13077 USA
//    672;MediaView;Claus Matthies;Bergweg 1a Giggenhausen 85376 Germany
//    673;VDA Elettronica;Tonello Fulvio;Viale Lino Zanussi, 3 Pordenone, 33170 Italy
//    674;CSS, Inc.;Thanh Chiem;151 Superior Blvd. Unit 13 & 14 Mississauga, Ontario L5T 2L1 Canada
//    675;Tek-Air Systems, Inc.;Robert Newton;43 Eagle Road Danbury, CT 06810 USA
//    676;ICDT;Eric Chang;2F, No. 168 Shuiyuan St. Shulin Dist New Taipei City, 238 Taiwan
//    677;The Armstrong Monitoring Corporation;Wil Moloughney;215 Colonnade Road South Ottawa, Ontario K2E 7K3 Canada
//    678;DIXELL S.r.l;Roberto Di Tommaso;Via dell'industria, 27 Pieve d'Alpago, Belluno 32010 Italy
//    679;Lead System, Inc.;Shunji Aoki;Lead System, Inc. 2F Daini-Ohgiya Buil. 3-25-8 Sugamo Toshima Word Tokyo, 170-0002 Japan
//    680;ISM EuroCenter S.A.;Marcin Ploski;Wyczolki 71 Street Warsaw 02-820 Poland
//    681;TDIS;Mike Sussman;Unit 10 Concept Park Innovation Close Poole, Dorset BH12 4QT United Kingdom
//    682;Trade FIDES;Vaclav Lukas;Dornych 57 Brno, 617 00 Czech Republic
//    683;Knürr GmbH (Emerson Network Power);Wolfram Petritz;Mariakirchener Straße 38 Arnstorf, 94424 Germany
//    684;Resource Data Management;Alan McBride;80 Johnstone Avenue Hillington, Glasgow G52 4NZ United Kingdom
//    685;Abies Technology, Inc.;YS Chen;6F, No. 237, Sec. 2, Sichuan Rd. Banqiao Dist. New Taipei City, 220 Taiwan
//    686;UAB Komfovent;Piotras Jenkinas;Ozo str. 10 Vilnius Lithuania
//    687;MIRAE Electrical Mfg. Co., Ltd.;Bong Jin Lee;SK Technopark Mega-208  Sangdaewondong, Jungwongu, Seongnamsi Gyeonggido, 432-721 Korea
//    688;HunterDouglas Architectural Projects Scandinavia ApS;Lars Bo Hansen;Hestehavevej 22 5856 Ryslinge CVR-nr., 32891802 Denmark
//    689;RUNPAQ Group Co., Ltd;Zeng Linchun;No. 10 Xiyuan-1 Road Hangzhou City, 310030 China
//    690;Unicard SA;Anna Gagaczowska;Lagiewnicka 54 Street Krakow, 30-417 Poland
//    691;IE Technologies;Gregory Tropsa;9351 Eastman Park Drive, Ste B Windsor, CO 80550 USA
//    692;Ruskin Manufacturing;Josiah Wiley;3900 Dr. Greaves Road Grandview, MO 64303 USA
//    693;Calon Associates Limited;David Blyth;2 Whitworth Court Manor Farm Road Runcorn, WA7 1WA United Kingdom
//    694;Contec Co., Ltd.;Kazuyoshi Nishiyama;3-9-31, Himesato,  Nishiyodogawa-ku-Osaka, 555-0025 Japan
//    695;iT GmbH;Klaus Guetter;An der Kaufleite 12 Kalchreuth, D-90562 Germany
//    696;Autani Corporation;Randy Clayton;7170 Riverwood Drive Ste B Columbia, MD 21046 USA
//    697;Christian Fortin;Christian Fortin;856 du Mont Owl's Head Sherbrooke, QC J1L 2Z5 Canada
//    698;HDL;Zeng Yi;24 JianZhong Road  Tianhe Development Zone of High&New Technology Est 510665 China
//    699;IPID Sp. Z.O.O Limited;Michal Mysliwiec;ul. Zawila 69 Krakow 30-390 Poland
//    700;Fuji Electric Co., Ltd;Elki Iwabuchi;5520 Minami Tamagaki-cho Suzuka City, Mie-ken 513-8633 Japan
//    701;View, Inc.;Steve Brown;195 S. Milpitas Blvd Milpitas, CA 95035 USA
//    702;Samsung S1 Corporation;Park Jun Hyun;#168, Sunhwa-dong, Jung-gu Seoul, 100-773 Korea
//    703;New Lift;Simon Baker;Lochhamer Schlag 8 82166 Gräfelfing Germany
//    704;VRT Systems;Mark Oellermann;38b Douglas Street Milton, QLD 4064 Australia
//    705;Motion Control Engineering, Inc.;Jeffrey Counts;11380 White Rock Road Rancho Cordova, CA 95742 USA
//    706;Weiss Klimatechnik GmbH;Edwin Zijlstra;Greizer Straße 41-49 Reiskirchen-Lindenstruth, 35447 Germany
//    707;Elkon;Andre Harel;1660, 55th Ave Lachine, Quebec H8T 3J5 Canada
//    708;Eliwell Controls S.r.l.;Diego Contiero;Via dell'industria, 15 Pieve d'Alpago (BL) Italy
//    709;Japan Computer Technos Corp;Toshimitsu Nagayasu;3-5-7 Kudan-Minami Chiyoda-ku Toyko 102-0074 Japan
//    710;Rational Network ehf;Throstur Jonsson;Kaupvangur 6 700 Egilsstadir Iceland
//    711;Magnum Energy Solutions, LLC;Mike Giorgi;43 Village Way Suite 209 Hudson, OH 44236 USA
//    712;MelRok;Gary Robbins;241 Ridge Street, Suite 350 Reno, NV 89501 USA
//    713;VAE Group;Rav Panchalingam;2/236 Arthur Street Newstead, QLD 4006 Australia
//    714;LGCNS;Inho Kim;23F, FKI Building 24, Yeoui-daero Yeongdeungpo-gu, Seoul 150-881 Korea
//    715;Berghof Automationstechnik GmbH;Holger Liedermann;Harretstraße 1 72800 Eningen Germany
//    716;Quark Communications, Inc.;Adam Guzik;2033B San Elijo Ave Unit 290 Cardiff, CA 92007 USA
//    717;Sontex;Matthias Sieber;Rue de la Gare 27 2605 Sonceboz Switzerland
//    718;mivune AG;Andre Wuest;Brandstrasse 33 CH-8952 Schlieren Switzerland
//    719;Panduit;Robert Dennelly;18900 Panduit Dr Tinley Park, IL 60487 USA
//    720;Smart Controls, LLC;David Kniepkamp;10000 Saint Clair Avenue Fairview Heights, IL 62208 USA
//    721;Compu-Aire, Inc.;Mahendra Ahir;8167 Byron Road Whittier, CA 90606 USA
//    722;Sierra;Scott Rouse;5 Harris Court Building L Monterey, CA 93940 USA
//    723;ProtoSense Technologies;Vijayendra Shamrao;No. 314, Vishal Soudha 9th Main 25th Cross, Banashankari 2nd Stage Bangalore, 560070 India
//    724;Eltrac Technologies Pvt Ltd;K. Gurudath;#63/3-4, P&T Colony Road Srigandhakaval Magadi Road Bangalore, 560 091 India
//    725;Bektas Invisible Controls GmbH;Cuneyt Bektas;Alte Bruchsaler Str. 28 Wiesloch, 69168 Germany
//    726;Entelec;Miet Loix;Wetenschapspark 25 Diepenbeek, 3590 Belgium
//    727;INNEXIV;Kamran Saleem;P.O. Box 3255 Oakton, VA 22124 USA
//    728;Covenant;Leon Cao;105 Jianguo Middle Road Shanghai China
//    729;Davitor AB;Torbjorn Carlqvist;Othemsvagen 3 Slite, 624 49 Sweden
//    730;TongFang Technovator;Zhang Jiwei;TongFang Technovator 21F-23F, Tower A Tsinghua Tongfang Hi-tech Plaza No. 1 Beijing, 100083 China
//    731;Building Robotics, Inc.;Stephen Dawson-Haggerty;360 17th St, Ste 204 Oakland, CA 94612 USA
//    732;HSS-MSR UG;Theodor Dotterweich;In Vorra 3 Frensdorf 96158 Germany
//    733;FramTack LLC;Nathaniel Frampton;7 Sherwood Place Wharton, NJ 07885 USA
//    734;B. L. Acoustics, Ltd.;Paul Brooker;152 Enterprise Court Eastways Witham, Essex CM8 2LA United Kingdom
//    735;Traxxon Rock Drills, Ltd;Tim Doucette;2780 Norland Ave Burnaby, BC V5B 3A6 Canada
//    736;Franke;Torsten Foels;Parkstrasse 1-5 Ludwigsfelde, D-14975 Germany
//    737;Wurm GmbH & Co;Andreas Neuhaus;Morsbachtalstr. 30 Remscheid, 42857 Germany
//    738;AddENERGIE;David Prevost;2327, boulevard du Versant Nord Bureau 120 Quebec, G1N 4C2 Canada
//    739;Mirle Automation Corporation;Chin Chao Yang;No. 3, R&D Rd. II Science Park Hsinchu, 30076 Taiwan
//    740;Ibis Networks;Michael Pfeffer;828 Fort Street Mall Suite 600 Honolulu, HI 96813 USA
//    741;ID-KARTA s.r.o.;Ing. Zdenek Kolba;Hlavni 3 Opava, 747 70 Czech Republic
//    742;Anaren, Inc.;Mark Burdick;6635 Kirkville Rd E. Syracuse, NY 13057 USA
//    743;Span, Incorporated;Ronald Kappeler;4404 Guion Road Indianapolis, IN 46254 USA
//    744;Bosch Thermotechnology Corp;Jeffrey Mills;50 Wentworth Ave Londonderry, NH 03053 USA
//    745;DRC Technology S.A.;Benoit Jemine;39, rue du Faubourg Kayl, L-3641 Luxembourg
//    746;Shanghai Energy Building Technology Co, Ltd;Zhou Shihui;Room 7A, No. 285 Changshou Rd Shanghai China
//    747;Fraport AG;Stefan Thomanek;Frankfurt Airport Services Worldwide  60547 Frankfurt/Main Germany
//    748;Flowgroup;Bryan Powell;Unit 5, Capenhurst Technology Park Capenhurst, Chester CH1 6EH United Kingdom
//    749;Skytron Energy, GmbH;Torsten Schlaaff;Ernst-Augustin-Str. 12 Berlin, D-12489 Germany
//    750;ALTEL Wicha, Golda Sp. J.;Radoslaw Wicha;ul. Luzycka 107 Krakow, 30-693 Poland
//    751;Drupal;David Thompson;15 Pine Shore Drive Brevard, NC 28712 USA
//    752;Axiomatic Technology, Ltd;David Moore;Graphic House Noel Street Kimberley, Nottingham NG16 2NE United Kingdom
//    753;Bohnke + Partner;Hendrik Baer;Industrieweg 13 Bergisch, Gladbach D-51429 Germany
//    754;Function1;Sandeep Khanej;1200 18th St., NW Suite 700 Washington DC 20036 USA
//    755;Optergy Pty, Ltd;Marc Abdelahad;22 McIntyre Road Burwood East Victoria, 3125 Australia
//    756;LSI Virticus;Mark Wagoner;15268 NW Greenbrier Pkwy Beaverton, OR 97006 USA
//    757;Konzeptpark GmbH;Andreas Ascheneller;Georg-Ohm-Str. 2 Lahnau, D-35633 Germany
//    758;Hubbell Building Automation, Inc.;Pete Baselici;9601 Dessau Road Austin, TX 78754 USA
//    759;eCurv, Inc.;Brian Mottershead;One Broadway 14th Floor Cambridge, MA 02142 USA
//    760;Agnosys GmbH;Stephan Marnul;8054 Graz, Ankerstr 6 Austria
//    761;Shanghai Sunfull Automation Co., LTD;Chen Chong;Room 803, No. 21, Nong 301 Haipeng Road Pudong, Shanghai 201209 China
//    762;Kurz Instruments, Inc.;Ruth Ward;2411 Garden Road Monterey, CA 93940 USA
//    763;Cias Elettronica S.r.l.;Romano Manzoli;Via Durando 38 Milano, 20158 Italy
//    764;Multiaqua, Inc.;Stephen Love;306 Hagood Street Easley, SC 29640 USA
//    765;BlueBox;Francesco Artusi;Via Valletta, 5 Cantarana di Cona, 30010 Italy
//    766;Sensidyne;Howard Mills;1000 112th Circle North Ste 100 St. Petersburg, FL 33716 USA
//    767;Viessmann Elektronik GmbH;Frank Fritz;Beetwiese 2 Allendorf, 35108 Germany
//    768;ADFweb.com srl;Daniele Dalla Torre;via Strada Nuova, 17 Mareno di Piave (TV), 31010 Italy
//    769;Gaylord Industries;Jim Christle;10900 SW Avery St Tualatin, OR 97062 USA
//    770;Majur Ltd.;Hrvoje Sodan;Fallerovo 20 Zagreb, 10000 Croatia
//    771;Shanghai Huilin Technology Co., Ltd.;Su Chao;Rm 1102, Block D New Century Plaza 48 Xingyi Road Changning, Shanghai 200050 China
//    772;Exotronic;Eric Roseman;PO Box 654 Constantia, 7848 South Africa
//    773;SAFECONTROL s.r.o.;Tomas Cizinsky;315/7 Vanickova Praque 6, 169 00 Czech Republic
//    774;Amatis;Jeffrey Davlyn;210 Aspen Business Center, Suite A Aspen, CO 81611 USA
//    775;Universal Electric Corporation;Paul Altimore;168 Georgetown Road Canonsburg, PA 15317 USA
//    776;iBACnet;John Schmidt;Avenue Louis-Ruchonnet 3 Lausanne, CH-1003 Switzerland
//    777;Reserved for ASHRAE;;
//    778;Smartrise Engineering, Inc.;Randy Glover;8360 Rovana Circle, #3 Sacramento, CA 95828 USA
//    779;Miratron, Inc.;Rodrick Seely;16420 SW 72nd Ave Porland, OR 97224 USA
//    780;SmartEdge;Robert Rodenhaus;4 Peuquet Parkway Tonawanda, NY 14150 USA
//    781;Mitsubishi Electric Australia Pty Ltd;Daryl Khoh;348 Victoria Road Rydalmere, N.S.W. 2116 Australia
//    782;Triangle Research International Ptd Ltd;Wee Chong Yew;10 Ubi Cresent #05-83 Ubi Techpark Singapore, 408564 Singapore
//    783;Produal Oy;Petri Hakkarainen;Keltakalliontie 18 Kotka, 48770 Finland
//    784;Milestone Systems A/S;Paolo Blem;Banemarksvej 50C Broendby, DK-2605 Denmark
//    785;Trustbridge;David Dixon;Warnford Court 29  Throgmorton Street London, EC2N 2AT United Kingdom
//    786;Feedback Solutions;Chandan Chowdhury;3rd Floor, 7111 Syntex Drive Mississauga, ON L5M 8C3 Canada
//    787;IES;Frank Ableson;410 Park Ave 15th Floor New York, NY 10022 USA
//    788;ABB Power Protection SA;Michele Sargenti;Via Luserte Sud 9 6572 Quartino Switzerland
//    789;Riptide IO;Shawn Leimbrock;315 Meigs Road Suite 110 Santa Barbara, CA 93109 USA
//    790;Messerschmitt Systems AG;Hartmut Messerschmitt;Laufamholzstr. 452 Nuremberg, D-90482 Germany
//    791;Dezem Energy Controlling;Torben Foerster;Sybelstrasse 63 Berlin, D-10629 Germany
//    792;MechoSystems;Alex Greenspan;4203 35th St Long Island City, NY 11101 USA
//    793;evon GmbH;Roman Ruthofer;Frank-Stronach-Str. 8 Gleisdorf, 8200 Austria
//    794;CS Lab GmbH;Dieter Schmitz;Römerstraße 15 Krefeld, 47809 Germany
//    795;8760 Enterprises, Inc.;Jason Alvarez;250 West 26th St 2nd Floor New York, NY 10001 USA
//    796;Touche Controls;Michael Picco;127 W. Wayne Street, Suite 300 Fort Wayne, IN 46802 USA
//    797;Ontrol Teknik Malzeme San. ve Tic. A.S.;Murat Egrikavuk;Turcan Caddesi 17 Serifali, Istanbul 34775 Turkey
//    798;Uni Control System Sp. Z o.o.;Antoine Nabayaogo;Ul. Kartuska 391A Gdansk, 80-125 Poland
//    799;Weihai Ploumeter Co., Ltd;David Wren;No. 576, South Torch Road Hancui District Weihai, China
//    800;Elcom International Pvt. Ltd;Mahesh Mudholkar;413/1/1A, Elcom House M.B. Lohia Marg, Gandhinager Kolhapur, Maharashtra 416 119 India
//    801;Signify;Oscar Deurloo;Building HTC-48 Professor Holstlaan 4 5656AE Eindhoven Netherlands
//    802;AutomationDirect;Joe Kimbrell;3505 Hutchinson Road Cumming, GA 30542 USA
//    803;Paragon Robotics;Julian Lamb;27331 Tungsten Rd Euclid, OH 44132 USA
//    804;SMT System & Modules Technology AG;Rudolf Honegger;Frohwiesstrasse 43 Rüti, CH-8630 Switzerland
//    805;OS Technology Service and Trading Co., LTD;Brian Pham;810.10, 26 Nguyen Thuong Hien Ward 1 Go Vap District Ho Chi Minh City Vietnam
//    806;CMR Controls Ltd;Neil Robertson;22 Repton Court Repton Close Basildon, SS13 1LN United Kingdom
//    807;Innovari, Inc.;Eric Pierce;19720 NW Tanasbourne Dr Ste 320 Hillsboro, OR 97124 USA
//    808;ABB Control Products;Tobias Gentzell;Motorgrand 20 Vasteras, SE-721 61 Sweden
//    809;Gesellschaft fur Gebäudeautomation mbH;Thomas Schmidt;St.-Georg-Str. 6 Dasing, 86453 Germany
//    810;RODI Systems Corp.;Stan Lueck;936 Hwy. 516 Aztec, NM 87410-2828 USA
//    811;Nextek Power Systems;Ben Hartman;461 Burroughs Street Detroit, MI 48202 USA
//    812;Creative Lighting;Lance Stewart;4 Pine Street North Ipswich Queensland, 4305 Australia
//    813;WaterFurnace International;Jason Bose;9000 Conservation Way Fort Wayne, IN 46809 USA
//    814;Mercury Security;Michael Serafin;2355 Mira Mar Ave Long Beach, CA 90815 USA
//    815;Hisense (Shandong) Air-Conditioning Co., Ltd.;Dong Jianhua;No. 1 Hisense Road, Nancun, Pingdu, Qingdao Shandong Province China
//    816;Layered Solutions, Inc.;Jerry Geis, Owner/President;2840 Old Vines Drive Westfield, IN 46074 USA
//    817;Leegood Automatic System, Inc.;Chao-Chou Hsiao;6F-6 No 4. Lane 609 Sec 5 San Chung Dist New Taipei City Taiwan
//    818;Shanghai Restar Technology Co., Ltd.;Lü Ming;Room 333, Building 6, No. 1006 Jin Sha Jiang Rd Putuo, Shanghai China
//    819;Reimann Ingenieurbüro;Thomas Reimann;Gustav-Ricker-Str. 62 Magdeburg, D-39102 Germany
//    820;LynTec;Alan Tschirner;8401 Melrose Drive Lenexa, KS 66214 USA
//    821;HTP;Dave Davis;120 Braley Road East Freetown, MA 02717 USA
//    822;Elkor Technologies, Inc.;Paul Korzycki;Elkor Technologies, Inc. 6 Bainard Street London, Ontario N6P 1A8 Canada
//    823;Bentrol Pty Ltd;Chris Frankel;PO Box 5062 Sandhurst East, VIC 3550 Australia
//    824;Team-Control Oy;Tommi Mäkelä;Uitontie 6 Meruarvi, 86220 Finland
//    825;NextDevice, LLC;Daniel Niewirowicz;715 Spencer Street Brighton, MI 48116 USA
//    826;GLOBAL CONTROL 5 Sp. z o.o.;Tomasz Bal;Stefana Czarnieckiego 72 Street Warsaw, 01-541 Poland
//    827;King I Electronics Co., Ltd;Mark Chang;6F, No. 495, Chung Cheng Road, Xindian District, New Taipei City, Taiwan, 23148 China
//    828;SAMDAV;Ricardo Galnares;Cerrada de Lerdo 6-E Distrito Federal, CP D.F. 10580 Mexico
//    829;Next Gen Industries Pvt. Ltd.;Ajay Singh;#1, 1st Floor, Opp. Shyam Garments Old Delhi Road, Sector-14 Gurgaon, Haryana 122001 India
//    830;Entic LLC;Tom John;2084 Johnson St Suite 116 Pembroke Pines, FL 33029 USA
//    831;ETAP;Joao Dias;Antwerpsesteenweg 130-B  2390-Malle Belgium
//    832;Moralle Electronics Limited;Stuart Allen;Unit 3 Firbank Court Firbank Way Leighton Buzzard, Bedfordshire LU7 4YJ United Kingdom
//    833;Leicom AG;Harald Stoerk;Harzachstrasse 5  Winterhur, CH-8404 Switzerland
//    834;Watts Regulator Company;Jeff Scilingo;815 Chestnut Street  North Andover, MA 01845 USA
//    835;S.C. Orbtronics S.R.L.;Gabriel Mihaila;Aurel Vlaicu Street No. 8 Loc. Albina Tichilesti, Braila 817171 Romania
//    836;Gaussan Technologies;Clark Gunness;5837 Beauregard Drive  Nashville, TN 37215 USA
//    837;WEBfactory GmbH;Stefan Hauck;Hollergasse 15  Buchen D-74722 Germany
//    838;Ocean Controls;Nicholas Jones;14 Miles Grv  Seaford, Vic 3198 Australia
//    839;Messana Air-Ray Conditioning s.r.l.;Andrea Ferrarelli;Via Amman 32/34  Cordenons-PN, 33084 Italy
//    840;Hangzhou BATOWN Technology Co. Ltd.;Zhang Yaoxiang;7F, No. 2203 Hangzhou  ShiQiao Road 279 China
//    841;Reasonable Controls;Artur Miller;ul. Waniliowa 33  Kamionki, 62-023 Poland
//    842;Servisys, Inc.;Christian Tremblay;12, Rue du Pacifique Est  Bromont, Quebec J2L 1J5 Canada
//    843;halstrup-walcher GmbH;Thomas Reif;Stegener Str. 10  Kirchzarten, D-79199 Germany
//    844;SWG Automation Fuzhou Limited;Lin Cheng;Room 208, No. 8 Building Zone B, Software Park Fuzhou  China
//    845;KSB Aktiengesellschaft;Joachim Schullerer;Johann-Klein-Straße 9  Frankenthal, 67227 Germany
//    846;Hybryd Sp. z o.o.;Piotr Pelka;Sikorskiego Street 28  Pyskowice, 44-120 Poland
//    847;Helvatron AG;Urs Hirt;Riedstrasse 7  Cham, 6330 Switzerland
//    848;Oderon Sp. Z.O.O.;Pawel Ropiak;Budryka, 8 Sosnowiec, 41-200 Poland
//    849;mikolab;Miroslaw Kot;ul. Franciszka Marii Lanciego 7b/2  Warsaw, 02-796 Poland
//    850;Exodraft;Thomas Irming;C. F. Tietgens Boulevard 41  Odense S0, 5220 Denmark
//    851;Hochhuth GmbH;Sebastian Martin;Rheingaustr. 190-196  Wiesbaden, 65203 Germany
//    852;Integrated System Technologies Ltd.;Geoff Archenhold;Serenity House 31 Gate Lane Sutton Coldfield, West Midlands B73 5TR United Kingdom
//    853;Shanghai Cellcons Controls Co., Ltd;Guo Qihui;Room 402A, No. 168 Luoyang Rd Shanghai,  China
//    854;Emme Controls, LLC;Dave Schiopucie;32 Valley St  Bristol, CT 06010 USA
//    855;Field Diagnostic Services, Inc.;Todd Rossi;10 Canal Street Suite 344  Bristol, PA 19007 USA
//    856;Ges Teknik A.S.;Muzaffer Kazakoglu;Girne M. Irmak S. Kucukyali Is Merkezi C-10  Maltepe, Istanbul 34852 Turkey
//    857;Global Power Products, Inc.;Mark Matyac;225 Arnold Road  Lawrenceville, GA 30044 USA
//    858;Option NV;Martin Croome;Gaston Geenslaan 14-3001  Leuven,  Belgium
//    859;BV-Control AG;Robin Hohl;Russikerstrasse 37  Fehraltorf, CH-8320 Switzerland
//    860;Sigren Engineering AG;Christian Kurz;Industriestrasse 57  Glattbrugg, 8152 Switzerland
//    861;Shanghai Jaltone Technology Co., Ltd.;Fabin Zhong;21F, NO. 345 Jinxiang Road  Shanghai China
//    862;MaxLine Solutions Ltd;Zhou Mi;8th Floor, On Hing Building 1 On Hing Terrace Central,  Hong Kong
//    863;Kron Instrumentos Elétricos Ltda;Andres Tavil;Rua Alexandre de Gusmao 278  Sao Paulo, 04760-020 Brazil
//    864;Thermo Matrix;Jeff Hoogveld;105-740 McCurdy Road  Kelowna, BC V1X 2P8 Canada
//    865;Infinite Automation Systems, Inc.;Joel Haggar;4383 N 119th St  Lafayette, CO 80026 USA
//    866;Vantage;Harold Jepsen;1061 South 800 East  Orem, UT 84062 USA
//    867;Elecon Measurements Pvt Ltd;Ubayathulla Ahamed;#764 4th Phase, 707 Yelahanka New Town  Bangalore, Karnataka 560-064 India
//    868;TBA;Henry Chang;15F, No. 145, Ren'ai Rd Xizhi Dist. New Taipei City, 22164 Taiwan
//    869;Carnes Company;;448 South Main St  Verona, WI 53593-1499 USA
//    870;Harman Professional;Robert Noble;3000 Research Drive  Richardson, TX 75082 USA
//    871;Nenutec Asia Pacific Pte Ltd;Stanley Toh;7030 Ang Mo Kio Ave 5 #08-055 Northstar@AMK, 569880 Singapore
//    872;Gia NV;Leen Geebelen;Industrieterrein Kanaal Noord 1161  Bree, 3960 Belgium
//    873;Kepware Tehnologies;Brett Austin;400 Congress Street  Portland, ME 04101 USA
//    874;Temperature Electronics Ltd;Simon Justyn;Unit 2, Wren Nest Road  Glossop, SK13 8HB United Kingdom
//    875;Packet Power;Paul Bieganski;2716 Summer Street, NE  Minneapolis, MN 55413 USA
//    876;Project Haystack Corporation;Richard McElhinney;1513 Hanover Avenue  Richmond, VA 23220 USA
//    877;DEOS Controls Americas Inc.;Henrik Plueth;4010 1st Ave. Burnaby BC V5C 3W4 Canada
//    878;Senseware Inc;Nathan Sacks;1751 Pinnacle Drive McLean, VA 22201 USA
//    879;MST Systemtechnik AG;Lukas Dillier;Airport Business Center 60 Belp, CH-3123 Switzerland
//    880;Lonix Ltd;Anu Katka;Hameentie 153 C Helsinki, FI-00560 Finland
//    881;GMC-I Messtechnik GmbH;Eike Weiss;Suedwestpark 15 Nuremberg, 90449 Germany
//    882;Aviosys International Inc.;Thomas Chiu;9F, No. 101, Pan-Hsin Road Pan-Chiao, Taipei 22066 Taiwan
//    883;Efficient Building Automation Corp.;Terry Sprangers;#1004-7495 132nd Street Surrey, BC V3W 1JB Canada
//    884;Accutron Instruments Inc.;Mike Sharkey;11 Mary Street Unit B Sudbury, ON P3C 1B4 Canada
//    885;Vermont Energy Control Systems LLC;Bill Kuhns;288 Sand Road  North Ferrisburgh, VT 05456 USA
//    886;DCC Dynamics;Heather Granneman;316 N. Trayer Ave. Glendora, CA 91741 USA
//    887;B.E.G. Brück Electronic GmbH;Friedrich Brück;Gerberstraße 33 Lindlar, D-51789 Germany
//    888;Reserved for ASHRAE;;
//    889;NGBS Hungary Ltd.;Zsolt Banko;Budaorsi ut 153 Budapest, H-11-1112 Hungary
//    890;ILLUM Technology, LLC;James Rhodes;2120 E. Sixth Street, Suite 16 Tempe, AZ 85281 USA
//    891;Delta Controls Germany Limited;Dusko Lukanic-Simpson;Fasanenweg 17 b Leinfelden-Echterdingen, D-70771 Germany
//    892;S+T Service & Technique S.A.;Frédéric Hess;67 route du Pas-de-l'Echelle Veyrier, 1255 Switzerland
//    893;SimpleSoft;Sudhir Pendse;257 Castro Street Suite 220 Mountain View, CA 94041 USA
//    894;Altair Engineering;Mike Anderson, VP Engineering;1820 E Big Beaver Road Troy, MI 48083 USA
//    895;EZEN Solution Inc.;Lee Hocheol;1F 3-12 Ogeum-ro 59 gil Songpa-gu Seoul, 05744 Korea
//    896;Fujitec Co. Ltd.;Masataka Sugano;Big Wing Hikone, Shiga 522-8588 Japan
//    897;Terralux;Paul Schroeter;1830 Lefthand Circle Suite B Longmont, CO 80501 USA
//    898;Annicom;Antoni Gates;Unit 21 Highview Highstreet Bordon, Hampshire GU35 0AX United Kingdom
//    899;Bihl+Wiedemann GmbH;Bernhard Wiedemann;Flosswoethstr. 41 Mannheim, 68199 Germany
//    900;Draper, Inc.;David Elliott;411 S. Pearl Street Spiceland, IN 47385 USA
//    901;Schüco International KG;Florian Morris;Karolinenstrasse 1-15 Bielefeld, 33609 Germany
//    902;Otis Elevator Company;Shari Parillo;5 Farms Springs Road Farmington, CT 05032 USA
//    903;Fidelix Oy;Jussi Rantanen;Martinkyläntie 41 Vantaa, 01720 Finland
//    904;RAM GmbH Mess- und Regeltechnik;Thomas Hain;Gewerbestr. 3 Herrsching am Ammersee, 82211 Germany
//    905;WEMS;Andrew Bishop;The Mission, Wellington Street Stockport, SK1 3AH United Kingdom
//    906;Ravel Electronics Pvt Ltd;K. Rajasekaran;150-A, Electronics Industrial Estate Perungudi, Chennai 600 096 India
//    907;OmniMagni;Jack Lee;2F-1, No. 130, Sec. 4 Nanjing E. Rd, Songshan Dist. Taipei City, 10553 Taiwan
//    908;Echelon;Apurba Pradhan;2901 Patrick Hency Drive Santa Clara, CA 95054 USA
//    909;Intellimeter Canada, Inc.;Warren Beacom;1125 Squires Beach Road Pickering, ON L1W 3T9 Canada
//    910;Bithouse Oy;Janne Pehkonen;Pirkkala, FI-33950 Finland
//    911;Reserved for ASHRAE;;
//    912;BuildPulse;Jason Burt;605 1st Ave. Ste 220 Seattle, WA 98104 USA
//    913;Shenzhen 1000 Building Automation Co. Ltd;Pingzhong Li;6E, 2nd, Chengxinhuating Building Shifu Rd, Buji Street Shenzhen, Guangdong Province China
//    914;AED Engineering GmbH;Nicole Wenzel;Taunusstr. 51 München, 80807 Germany
//    915;Güntner GmbH & Co. KG;Markus Jabs;Hans-Güntner-Straße 2-6 Fürstenfeldbruck,, 82256 Germany
//    916;KNXlogic;Christof De Backere;Beisbroekdreef 18 Brugge, 8200 Belgium
//    917;CIM Environmental Group;Colin Cullinan;Level 7, 1 Alfred Street Sydney, NSW 2000 Australia
//    918;Flow Control;Paul Kearney;PO Box 848 18715 141st Ave NE Woodinville, WA 98072 USA
//    919;Lumen Cache, Inc.;Derek Cowburn;13402 Chrisfield Lane McCordsville, IN 46055 USA
//    920;Ecosystem;Louis-Philippe Rose;Delta 3 Building 2875 Laurier Blvd, Ste 950 Quebec, G1V 2M2 Canada
//    921;Potter Electric Signal Company, LLC;Jeff Hendrickson;5757 Phantom Drive, Suite 125 Hazelwood, MO 63042 USA
//    922;Tyco Fire & Security S.p.A.;Alessio Di Gioia;Viale dell 'Innovazione, 3 Milano, 20126 Italy
//    923;Watanabe Electric Industry Co., Ltd.;Tomonari Hiraizumi;16-19, 6-chome, Jingumae Shibuya-ku, Tokyo 150-0001 Japan
//    924;Causam Energy;Tom Gordon;9208 Falls of Neuse Road, Suite 215 Raleigh, NC 27615 USA
//    925;W-tec AG;Ela Sibilska;Dornbachstraße 1a Bad Homburg, 61352 Germany
//    926;IMI Hydronic Engineering International SA;Jean-Christophe Carette;Terre-Bonne Business Park, Bat Z2 Route de Crassier 19 Eysins, CH-1262 Switzerland
//    927;ARIGO Software;Dirk Brinkmann;Osnabrücker Str. 1b 33649 Bielefeld Germany
//    928;MSA Safety;Chris Starta;1000 Cranberry Woods Drive Cranberry Township, PA 16066 USA
//    929;Smart Solucoes Ltda - MERCATO;Eduardo Ricalde;Rua Capistrano de Abreu, 89 Bairro Niterói Canoas - RS, 92120-130 Brazil
//    930;PIATRA Engineering;Peter Kalt;48 Union Street Erskineville, NSW 2043 Australia
//    931;ODIN Automation Systems, LLC;Erik Maseng;2 Townsend W, Unit 2 Nashua, NH 03063 USA
//    932;Belparts NV;Ludwig De Locht;Wingepark 4 Rotselaar, BE-3110 Belgium
//    933;UAB, SALDA;Andrius Tamasauskis;Ragain?s str. 100 Siauliai., LT-78109 Lithuania
//    934;Alre-IT Regeltechnik GmbH;Klaus Lorenz;Richard-Tauber-Damm 10 Berlin, 12277 Germany
//    935;Ingenieurbüro H. Lertes GmbH & Co. KG;Hermann Lertes;Georg-Treber-Str. 27 Rüsselsheim, 65428 Germany
//    936;Breathing Buildings;Shaun Fitzgerald;The Courtyard 15 Sturton Street Cambridge, CB1 2SN United Kingdom
//    937;eWON SA;Pierre Crokaert;Av. Robert Schuman 22 Nivelles, BE-1400 Belgium
//    938;Cav. Uff. Giacomo Cimberio S.p.A;Tiziano Guidetti;San Maurizio d'Opaglio, 28017 Italy
//    939;PKE Electronics AG;Erich Weber;Computerstraße 6, Wien, A-1100 Austria
//    940;Allen;Alan Davis;2100 Kramer Lane Suite 250 Austin, TX 78758 USA
//    941;Kastle Systems;Jaimie Crafton;6402 Arlington Boulevard Falls Church, VA 22042 USA
//    942;Logical Electro-Mechanical (EM) Systems, Inc.;James Spruell;885 Freemanwood Ln Milton, GA 30004 USA
//    943;ppKinetics Instruments, LLC;Tian Qi;40 E. Main Street Newark, DE 19711 USA
//    944;Cathexis Technologies;MC Randelhoff;259 Montpelier Road Morningside, Durban 4001 South Africa
//    945;Sylop sp. Z o.o. sp.k;Filip Dudek;Ul. Szlak 28/3 31-153 Krakow Poland
//    946;Brauns Control GmbH;Ingo Brauns;Westernstraße 12 Stadthagen, 31655 Germany
//    947;OMRON SOCIAL SOLUTIONS CO., LTD.;Kohei Chimura;86-1 IchimiYake Yasu-City, Shiga 920-2362 Japan
//    948;Wildeboer Bauteile Gmbh;Benjamin Frei;Marker Weg 11 Weener, 26826 Germany
//    949;Shanghai Biens Technologies Ltd;James Sun;Floor 5, Building 21 481 Guiping Road Shanghai, 200233 China
//    950;Beijing HZHY Technology Co., Ltd;BeiShan Li;Second Floor, Complex Building, Long Ze Yuan Hui Long-Guan, Changping District Beijing City China
//    951;Building Clouds;Robert Wallace;3229 Whipple Road  Union City, CA 94587 USA
//    952;The University of Sheffield-Department of Electronic and Electrical Engineering;Peter Rockett;Room PC10, Portobello Centre, Communications Research Group Sheffield, SI 4ET United Kingdom
//    953;Fabtronics Australia Pty Ltd;Steve Fallon;39-41 Canterbury Rd  Braeside, Vic 3195 Australia
//    954;SLAT;Denis Badoil;11, rue Jean ElysÃ©e Dupuy  Champagne au Mont d'Or, 69410 France
//    955;Software Motor Corporation;Trevor Creary;1295 Forgewood Avenue  Sunnyvale, CA 94089 USA
//    956;Armstrong International Inc.;Matt Nowak;816 Maple Street  Three Rivers, Michigan 49093 USA
//    957;Steril-Aire, Inc.;Sandy Klivans;2840 North Lima Street  Burbank, CA 91504 USA
//    958;Infinique;Arifa Riaz;513-4185 Shipp Drive  Mississauga, ON L4Z2Y8 Canada
//    959;Arcom;Patrick Tabouret;ZAC de la Loyere  Fragnes- La Loyere, 71530 France
//    960;Argo Performance, Ltd;Jason McGehee;2151 Newcastle Ave Cardiff by the Sea, CA 92007 USA
//    961;Dialight;John Sondericker III;1501 Route 34 South Farmingdale, NJ 07727 USA
//    962;Ideal Technical Solutions;Jose Beltran;3135 Prince Sultan Bin Abdulazia Road As Sulimaniyah, Riyadh Saudi Arabia
//    963;Neurobat AG;Geert Hoevenaars;Altenburgerstrasse 49 Brugg AG, CH-5200 Switzerland
//    964;Neyer Software Consulting LLC;Jack Neyer;6534 Dalzell Place Pittsburgh, PA 15217 USA
//    965;SCADA Technology Development Co., Ltd.;Pradit Kaewart;224/164 Saimai Road Saimai, Bangkok 10220 Thailand
//    966;Demand Logic Limited;Tom Randall;Here East International Broadcast Centre London, E20 3BS United Kingdom
//    967;GWA Group Limited;Boris Napernikov;7 Eagleview Place Eagle Farm, QLD 4009 Australia
//    968;Occitaline;Daniel Zotti;13 rue Antoine de Lavoisier Plaisance-du-touch, 31830 France
//    969;NAO Digital Co., Ltd.;Seungman Lee;1106, Kolon Digital Tower, Digital-ro 32gil 30, Seoul Korea
//    970;Shenzhen Chanslink Network Technology Co., Ltd.;Honggang Fu;16G, Block B, Reith International Yanhe North Road Luohu, Shenzhen China
//    971;Samsung Electronics Co., Ltd.;Kihwan Cho;129, Samsung-ro, Yeongtong-gu Suwon-si, Gyeonggi-do 16677 Korea
//    972;Mesa Laboratories, Inc.;Khoi Nguyen;12100 W. 6th Avenue Lakewood, CO 80228 USA
//    973;Fischer;Lars Elling;Am Hagelkreuz 3a Neuss, D-41469 Germany
//    974;OpSys Solutions Ltd.;Brad Lonergan;Level 5, 3 Ferncroft Street Grafton, Auckland New Zealand
//    975;Advanced Devices Limited;Chris Bao;805-806 Prosperity Millennia Plaze 663 King's Road, North Point Hong Kong, China
//    976;Condair;Robert Merki;Talstrasse 35-37 Pfaeffikon, CH-8808 Switzerland
//    977;INELCOM Ingenieria Electronica Comercial S.A.;Juan Garcia;Jose Isbert 16 Ciudad de la Imagen Pozuelo de Alarcon, 28223 Spain
//    978;GridPoint, Inc.;Michael Press;5305 Valley Park Dr. Ste #2 Roanoke, VA 24019 USA
//    979;ADF Technologies Sdn Bhd;Yong Hao;K-6-7, Solaris Mont' Kiara, No. 2 Jalan Solaris Kuala Lumpur, 50480 Malaysia
//    980;EPM, Inc.;Christopher Adam;2105 Power Lane Fulton, MO 65251 USA
//    981;Lighting Controls Ltd;Andrew French;5 Bourne Mill Business Park Guildford Road Farnham, Surrey GU9 9PS United Kingdom
//    982;Perix Controls Ltd.;Larry Cheng;6th Floor, Building 3 6 Cuibai Road Hangzhou, China
//    983;AERCO International, Inc.;Vincent D'Amore;100 Oritani Drive Blauvelt, NY 10913 USA
//    984;KONE Inc.;Jean-Christophe Almira;450 Century Parkway Sute 300 Allen, TX 75013 USA
//    985;Ziehl-Abegg SE;Ruben Kollmar;Heinz-Ziehl-Str  Kunzelsau, 74653 Germany
//    986;Robot, S.A.;Bernat Pons-Estel;Calle Gremi de Cirurgians I Barbers, 22 Industrial Poligono Son Rossinyol Palma de Mallorca, Balearic Islands 07009 Spain
//    987;Optigo Networks, Inc.;Dave Cousins;1200-555 West Hastings Street Vancouver, BC V6B 4N6 Canada
//    988;Openmotics BVBA;Pieter De Clerck;Hertstokweg 5-1741 Ternat Belgium
//    989;Metropolitan Industries, Inc.;Wayne Barkley;37 Forestwood Drive  Romeoville, IL 60446 USA
//    990;Huawei Technologies Co., Ltd.;Su Bensheng;Huawei Area H2-F4B-03-04 Bantian, Longgang District Shenzhen, 518129 China
//    991;OSRAM Sylvania, Inc.;Roy Harvey;200 Ballardvale Street Wilmington, MA 01887 USA
//    992;Vanti;Tom Haskell;44 Upper Gough Street  Birmingham, B1 1JL United Kingdom
//    993;Cree Lighting;Bob Rogers, IoT QA Manager;4401 Silicon Drive Durham, NC 27702 USA
//    994;Richmond Heights SDN BHD;Shamsul Zainudin;No. 44A, Jalan Renang 13/26 Seksyen 13 Shah Alam, Selangor Darul Ehsan 40100 Malaysia
//    995;Payne-Sparkman Lighting Mangement;Steve Payne;2571 Roanoke Avenue  New Albany, IN 47150 USA
//    996;Ashcroft;Tyler Bessette;250 East Main Street  Stratford, CT 06614 USA
//    997;Jet Controls Corp;Jim Dewar;PO Box 4013-3848 3rd Ave.  Smithers, BC V0J 2N0 Canada
//    998;Zumtobel Lighting GmbH;Klaus Vamberszky;Schweizerstrasse 30 Dornbirn, 6850 Austria
//    999;Reserved for ASHRAE;;
//    1000;Ekon GmbH;Weidacher Hartwig;St. Lorenznerstrasse, 2 Bruneck, 39031 Italy
//    1001;Molex;Dave Rios;2222 Wellington Court Lisle, IL 60532 USA
//    1002;Maco Lighting Pty Ltd.;Allan Organ;Unit 5, 9 Stockwell Place Archerfield QLD, 4108 Australia
//    1003;Axecon Corp.;German Akselrod;876 Jamaica Avenue Brooklyn, NY 11208 USA
//    1004;Tensor plc;Nigel Smith;Hail Weston House, Hail Weston St. Neots, Cambs PE19 5J7 United Kingdom
//    1005;Kaseman Environmental Control Equipment (Shanghai) Limited;Peter Yang;211-216 Building #4, 30 Hongcao Road, Xuhui District Shanghai, 200233 China
//    1006;AB Axis Industries;Edvardas Liakas;Kulautuvos str. 45a Kaunas, LT 47190 Lithuania
//    1007;Netix Controls;Jarno Mitjonen;P. O. Box 38 Helsinki, FI-00641 Finland
//    1008;Eldridge Products, Inc.;Mark Eldridge;465 Reservation Road Marina, CA 93933 USA
//    1009;Micronics;Michael Farnon;Knaves Beech Business Centre Davies Way Loudwater, Buckinghamshire HP10 9QR United Kingdom
//    1010;Fortecho Solutions Ltd;Robert Green;Solon House, 40A Peterborough Road London, SW6 3BN United Kingdom
//    1011;Sellers Manufacturing Company;Douglas Ritchie;918 West Walnut Street Danville, KY 40422 USA
//    1012;Rite-Hite Doors, Inc.;Ryan Beggs;4343 Chavenelle Road Dubuque, IA 52002 USA
//    1013;Violet Defense LLC;Mark Nathan;215 Celebration Place Suite 330 Celebration, FL 34747 USA
//    1014;Simna;Romuald Kliukovskij;Ozo 10, LT Vilnius, 08200 Lithuania
//    1015;Multi-Énergie Best Inc.;Michel Bergeron;7975, boulevard des Forges Trois-Rivieres, Quebec G8Y 1Z5 Canada
//    1016;Mega System Technologies, Inc.;Richard Long;2F, No. 41, Lane 76, Ruiguang Rd Neihu Dist Taipei City, 11491 Taiwan
//    1017;Rheem;Raheel Chaudhry;2600 Gunter Park Drive East Montgomery, AL 36109 USA
//    1018;Ing. Punzenberger COPA-DATA GmbH;Gunther Haslauer;Karolingerstrasse 7b Salzburg 5020 Austria
//    1019;MEC Electronics GmbH;Robert Haidinger;Dresdner Strasse 45/DG 1200 Wien Austria
//    1020;Taco Comfort Solutions;Timothy Davis;1160 Cranston Street Cranston, RI 02920 USA
//    1021;Alexander Maier GmbH;Rene Rieck;Beckstrasse 3 69412 Eberbach Germany
//    1022;Ecorithm, Inc.;Don Kasper;1919 State Street Suite 207 Santa Barbara, CA 93101 USA
//    1023;Accurro Ltd;Chris Smith;Suite 538, 162-168 Regent Street London, W1B 5TF United Kingdom
//    1024;ROMTECK Australia Pty Ltd;Philip Harman;37 Collingwood Street Osborne Park Western Australia 6017 Australia
//    1025;Splash Monitoring Limited;Andy Ryan;15B Saturn Place Albany, Auckland New Zealand
//    1026;Light Application;Matt James-Wallace;5/8 Hasler Road Osborne Park Western Australia, 6017 Australia
//    1027;Logical Building Automation;Anthony Stufano;3/68 Roberts Ave Mortdale, NSW 2223 Australia
//    1028;Exilight Oy;Matti Hatonen;Hermiankatu 6-8 A Tampere, FI-33720 Finland
//    1029;Hager Electro SAS;Silvano Maffessoli;132, Boulevard d'Europe Obernai, 67215 France
//    1030;KLIF Co., LTD;Daisuke Fujimoto;6-7-7 Minami-cho Nishitokyo-Shi, Tokyo 188-0012 Japan
//    1031;HygroMatik;M. Lütkemann;Lise-Meitner-Str. 3 24558 Henstedt-Ulzburg Germany
//    1032;Daniel Mousseau Programmation & Electronique;Daniel Mousseau;132 Av de l'amitie Rouyn-Noranda, Quebec J9Y 0E5 Canada
//    1033;Aerionics Inc.;Sanjib Baral;3601 N St Paul Avenue Sioux Falls, SK 57104 USA
//    1034;M2S Electronique Ltee;Etienne-Vincent Hardy;2855 rue de Celles Quebec, G2C 1K7 Canada
//    1035;Automation Components, Inc.;Bill Kubsh;2305 Pleasant View Road Middleton, WI 53562 USA
//    1036;Niobrara Research & Development Corporation;Scott Henson;PO Box 3418 Joplin, MO 64803 USA
//    1037;Netcom Sicherheitstechnik GmbH;Tobias Klein;Rheinallee 189 Mainz, 55120 Germany
//    1038;Lumel S.A.;Darlusz Tront;1 Sulechowska Street Zielona, Gora 65-022 Poland
//    1039;Great Plains Industries, Inc.;Greg Highfill;5252 E. 36th St. N Wichita, KS 67220 USA
//    1040;Domotica Labs S.R.L;Claudio Caldera;Via del Tiro a Segno 31 Mondowi (CN), 12084 Italy
//    1041;Energy Cloud, Inc.;John Carrieri;3525 Del Mar Heights Road, #370 San Diego, CA 92130 USA
//    1042;Vomatec;Thomas Schmuck;Riegelgrube 7 Bad Kreuznach, 55543 Germany
//    1043;Demma Companies;Mark Hodge;38 The Green Castle Bromwich Birmingham, B36 9AL United Kingdom
//    1044;Valsena;Audrius Janusauskas;Savanoriu Ave 271-412 Kaunas, LT-50131 Lithuania
//    1045;Comsys Bärtsch AG;Michel Seiler;Weingartenstrasse 11 Rüschlikon, CH-8803 Switzerland
//    1046;bGrid;Bruce Schult;612 Sarphatistraat AV, Amsterdam 1018 Netherlands
//    1047;MDJ Software Pty Ltd;Matthew James;22 Kia-Ora Parade Ferntree Gully Vic, 3156 Australia
//    1048;Dimonoff, Inc.;Daniel Noiseux;2022 Lavoisier Suite 175 Quebec, QC G1N 4L5 Canada
//    1049;Edomo Systems, GmbH;Stephan Eisler;Obertorplatz 2 Landau, 76829 Germany
//    1050;Effektiv, LLC;Darrel O'Pry;135 W. 27th Street New York, NY 10001 USA
//    1051;SteamOVap;Bernard Saint-Yves;33 Prince Montreal, QC H3C 2M7 Canada
//    1052;grandcentrix GmbH;Sven Gebhardt;Holzmarkt 1 Köln, D-50676 Germany
//    1053;Weintek Labs, Inc.;Maofan Hsu;9th Fl., No. 910, Zhongzheng Rd. Zhonghe District New Taipei City, 23586 Taiwan
//    1054;Intefox GmbH;Quirino Nardin;Baumgartstr. 4 Schwarzach, 6858 Austria
//    1055;Radius22 Automation Company;David Tesluk;1867 B Lower Craigmont Rd Combermere, Ontario K0J 1L0 Canada
//    1056;Ringdale, Inc.;John Montgomery;1009 Segundo Drive Georgetown, TX 78628 USA
//    1057;Iwaki America;Mark Pantazes;5 Boynton Road Hopping Brook Park Holliston, MA 07146 USA
//    1058;Bractlet;Brian Smith;510 S. Congress Ave Ste. 107 Austin, TX 78704 USA
//    1059;STULZ Air Technology Systems, Inc.;Casey Jon McKay;1572 Tilco Drive Frederick, MD 21704 USA
//    1060;Climate Ready Engineering Pty Ltd;Keya Joshi;56 58 Chapel Street Marrickville, NSW 2204 Australia
//    1061;Genea Energy Partners;Michal Pasula;19100 Von Karman Avenue Suite 550 Irvine, CA 92612 USA
//    1062;IoTall Chile;Rafael Mardones;Av. Providencia #1208 OF Santiago, 1603 Chile
//    1063;IKS Co., Ltd.;Satoshi Osawa;6F Karasuma enishi bld., 282 Makieya-cho Karasuma Nijyo-Agaru, Nakagyo-ku Kyoto, 604-0857 Japan
//    1064;Yodiwo AB;Per Martenson;Skeppsbron 28 Stockholm, 11130 Sweden
//    1065;TITAN electronic GmbH;Roland Unger;Gewerbepark 6 Wolfau, 7412 Austria
//    1066;IDEC Corporation;Ken Takeda;2-6-64, Nishi-Miyahara Yodogawa-ku, Osaka 532-0005 Japan
//    1067;SIFRI SL;Antonio Garcia;C/Septiembre, 36 Madrid, E-28022 Spain
//    1068;Thermal Gas Systems Inc.;Steven Morgan;11285 Elkins Rd. Bldg. H-1 Roswell, GA 30076 USA
//    1069;Building Automation Products, Inc.;Jeffrey Stoltenow;750 North Royal Ave. Gays Mills, WI 54631 USA
//    1070;Asset Mapping;Michael Grant;Build Studios 203 Westminster Bridge Road Waterloo, London SE1 7FR United Kingdom
//    1071;Smarteh Company;Miroslav Crv;Poljubinj 114 5220 Tomlin, Slovenia
//    1072;Datapod Australia Pty Ltd.;Mark Poulsen;99 Tennant Street Fyshwick, ACT 2609 Australia
//    1073;Buildings Alive Pty Ltd;Baden Hughes;Level 1, 86 Liverpool St. Sydney, NSW-2000 Australia
//    1074;Digital Elektronik;Johannes Auer;Berchtesgadner Straße 10 St. Leonhard, 5083 Austria
//    1075;Talent Automação e Tecnologia Ltda;Rogerio Zanotta;Rua das Casuarinas 108 São Paulo SP, 04321-100 Brazil
//    1076;Norposh Limited;Muhammad Sadqain;Workspace House, 28/29 Maxwell Road Peterborough, PE2 7JE United Kingdom
//    1077;Merkur Funksysteme AG;Patrick Isenschmid;Wassergrabe 14 Sursee, CH-6210 Switzerland
//    1078;Faster CZ spol. S.r.o;Tomáš Hora;Jarni 44g Brno, 61400 Czech Republic
//    1079;Eco-Adapt;Laurent Laparra;39, rue de Chateaudun Paris, 75009 France
//    1080;Energocentrum Plus, s.r.o;Martin Chlupá?;Technická 1902/2 160 00 Prague Czech Republic
//    1081;amBX UK Ltd;David Eves;5 Wells Place, Redhill Surrey, RH1 3DR United Kingdom
//    1082;Western Reserve Controls, Inc.;Jason White;1485 Exeter Dr. Akron, OH 44306 USA
//    1083;LayerZero Power Systems, Inc.;James Galm;1500 Danner Dr. Aurora, OH 44202 USA
//    1084;CIC Jan H?ebec s.r.o.;Jan H?ebec;Na Zlatß stezce 1075 Dob?íš, 263 01 Czech Republic
//    1085;Sigrov BV;Javier Lomas;Vijfde Polder 1 Wageningen, 6708WC Netherlands
//    1086;ISYS-Intelligent Systems;Robert Jarzabek;Wygoda 14 Karczew, 05-480 Poland
//    1087;Gas Detection (Australia) Pty Ltd;Christopher Kelly;Building K1, Room 105, USQ Site, West St. Queensland 4350 Australia
//    1088;Kinco Automation (Shanghai) Ltd.;Steven Song;Building 3, 26 Qiuyue Road, Zhangjiang Hi-Tech Park Shanghai, 201203 China
//    1089;Lars Energy, LLC;Randy Roy;36 Whittier Parkway Severna Park, MD 21146 USA
//    1090;Flamefast (UK) Ltd.;Andrew Green;1 Heaton Court Risley Road Warrington, WA 3 6QU United Kingdom
//    1091;Royal Service Air Conditioning;Tom Podgurski;1712 New Ave. San Gabriel, CA 91776 USA
//    1092;Ampio Sp. Z o.o.;Aleksander Kruszewicz;ul. Chopina 35 Szczecin, 71-450 Poland
//    1093;Inovonics Wireless Corporation;Mark Jarman;397 S. Taylor Ave. Louisville, CO 80027 USA
//    1094;Nvent Thermal Management;Paul Calton;11004 174th Street NW Edmonton, AB T5S 2P3 Canada
//    1095;Sinowell Control System Ltd;Wenping Ren;Room 103, Qiaozhong Middle Road Guangzhou, Guangdong China
//    1096;Moxa Inc.;Shian Li;Fl. 4, No. 135, Lane 235 Baoqiao Rd. Xindian Dist. New Taipei City, 23145 Taiwan
//    1097;Matrix iControl SDN BHD;Lim Soon-Keat;No. 10, Jalan DBP Dolomite Business Park Batu Caves, Selangor Darul Ehsan 68100 Malaysia
//    1098;PurpleSwift;Derek Noffke;69 Southfield Rd Plumstead, Cape Town South Africa
//    1099;OTIM Technologies;Eric Van Zant;7416 Sunset Drive Blaine, WA 98230 USA
//    1100;FlowMate Limited;KC Tsui;Unit 1202, 12/F, Malaysia Building 50 Gloucester Rd Wan Chai, Hong Kong
//    1101;Degree Controls, Inc.;Eric Zweighaft;18 Meadowbrook Dr. Milford, NH 03055 USA
//    1102;Fei Xing (Shanghai) Software Technologies Co., Ltd.;Wang YangYang;Room 601, No. 23, Area 88 JiangWangCheng Road Shanghai, China
//    1103;Berg GmbH;Thomas Stengl;Martinsried, 82152 Germany
//    1104;ARENZ.IT;Daniel Arenz;Bonner Talweg 333 Bonn, D-53129 Germany
//    1105;Edelstrom Electronic Devices & Designing LLC;Binoy Raphael;Abu Dhabi United Arab Emirates
//    1106;Drive Connect, LLC;Roy Moore;2842 Broadway Center Blvd. Brandon, FL 33510 USA
//    1107;DevelopNow;Wellisson Rogato;761-São Carlos SP 13562-070 Brazil
//    1108;Poort;Jaron Rademeyer;80 Ou Wapad Street, Ifaf Business Centre Shop 43 Hartbeespoortdam South Africa
//    1109;VMEIL Information (Shanghai) Ltd;Mae Zhao;Building 2, No 288, Ningfu Road Fengxian District Shanghai, 201203 China
//    1110;Rayleigh Instruments;Ryan Welshman;Raytel House, Cutlers Road South Woodham Ferrers Chelmsford, Essex CM3 5WA United Kingdom
//    1111;Reserved for ASHRAE;;
//    1112;CODESYS Development;Ullrich Meyer;Tobias-Dannheimer-Straße 5 Kempten, 87439 Germany
//    1113;Smartware Technologies Group, LLC;William Zehler;1000 Young Street, Suite 450 Tonawanda, NY 14150 USA
//    1114;Polar Bear Solutions;Justin Wells;51 Conifer Crest Newbury, Berkshire RG14 6RS United Kingdom
//    1115;Codra;Eric Oddoux;2 rue Christophe Colomb Massy, CS 0851-91300 France
//    1116;Pharos Architectural Controls Ltd;Dave Shaw;International House, 7 High Street Ealilng Broadway, London W5 5DB United Kingdom
//    1117;EngiNear Ltd.;Zoltan Orlovits;Komárom u. 21., Tatabánya 2800, Hungary
//    1118;Ad Hoc Electronics;Jan Finlinson;115 S State St., Suite B Lindon, UT 84042 USA
//    1119;Unified Microsystems;Christopher Morley;40th Floor, PBCom Tower 6795 Ayala Avenue Makati City Philippines
//    1120;Industrieelektronik Brandenburg GmbH;Christian Werner;Friedrichshafener Strasse 10 , 14772 Brandenburg an der Havel Germany
//    1121;Hartmann GmbH;Lutz Polosek;Frankenberger Str. 64 09661 Hainichen Germany
//    1122;Piscada;Steinar Fossen; Trondheim, N-7047 Norway
//    1123;KMB systems, s.r.o.;Milan Bleha;Dr. Milady Horakove 559 Liberec 7, 46006 Czech Republic
//    1124;PowerTech Engineering AS;Christian Lundheim;Gråterudveien 1 , 3036 Drammen Norway
//    1125;Telefonbau Arthur Schwabe GmbH & Co. KG;Daniel Kaumanns;Langmaar 25 , 41238 Mönchengladbach Germany
//    1126;Wuxi Fistwelove Technology Co., Ltd.;Tony Hu;No. 111 Linhu Street Xinwu District Wuxi, Jiangsu Province China
//    1127;Prysm;Thomas Polaert;Antelios Batiment E 75 rue Marcellin Berthelot Aix en Provence, 13858 France
//    1128;STEINEL GmbH;Torsten Born;Dieselstraße 80-84 Herzebrock-Clarholz, D-33442 Germany
//    1129;Georg Fischer JRG AG;Philippe Cachot;Hauptstrasse 130 Sissach, CH-4450 Switzerland
//    1130;Make Develop SL;Guiliermo Cuervo;Anade Real 11 Oleiros A Coruna, 15172 Spain
//    1131;Monnit Corporation;Kelly Lewis;3400 S West Temple South Salt Lake, UT 84115 USA
//    1132;Mirror Life Corporation;Minoru Toyoda;1-21-3, Kanda Nishiki-cho Chiyoda-ku, Tokyo 101-0054 601 Japan
//    1133;Secure Meters Limited;Ajit Jain;P. O. Box 30 Pratapnagar Industrial Area Udaipur, 313 003 India
//    1134;PECO;Marcia Christiansen;11241 SE Highway 212 Clackamas, OR 97015 USA
//    1135;.CCTECH, Inc.;Guy Watelle;2300, Lßon-Harmel Suite 106 Qußbec, G1N 4L2 Canada
//    1136;LightFi Limited;Alex Bak;130 Old Street London, EC1V 9BD United Kingdom
//    1137;Nice Spa;Marco Baschiera;Via Callalta, 1 31046 Oderzo (TV), Italy
//    1138;Fiber SenSys, Inc.;David Hollingsworth;2925 NE Aloclek Dr Ste 120 Hillsboro, OR 97124 USA
//    1139;B&D Buchta und Degeorgi;Buchta Peter;Dörflergasse 4 , 2504 Sooß Austria
//    1140;Ventacity Systems, Inc.;Joe McGowan;757 Arnold Drive Suite A Martinez, CA 94553 USA
//    1141;Hitachi-Johnson Controls Air Conditioning, Inc.;Yoshiyuki Yamanashi;390, Muramatsu Shimizuj-ku, Shizuoka-shi 424-0926 Japan
//    1142;Sage Metering, Inc.;Bob Steinberg;8 Harris Ct., D-1 Monterey, CA 93940 USA
//    1143;Andel Limited;Marshall Booth;New Mills ,Brougham Road, Marsden Huddersfield, West Yorkshire HD7 6AZ United Kingdom
//    1144;ECOSmart Technologies;Fang Liu;C505 Room, Zhongguancun Dongsheng Science and Technology Park Haidian District, Beijing China
//    1145;S.E.T.;Cecil Man;Room A, 21/f Chiap King Industrial Building No. 114 King Fuk Street San Po Kong, Kowloon Hong Kong
//    1146;Protec Fire Detection Spain SL;Luis Rocamora;9-11 Can Diners Argentona, Barcelona 08310 Spain
//    1147;AGRAMER UG;Tereza Kolendic-Kurtalj;Josephspitalstr. 15 München, Ust-idNr DE320378116 Germany
//    1148;Anylink Electronic GmbH;Norbert Kees;Max-Planck-Str. 2 Großmehring, 85098 Germany
//    1149;Schindler, Ltd;Adrian Buenter;Zugerstrasse 13 Ebikon, CH-6030 Switzerland
//    1150;Jibreel Abdeen Est.;Jibreel Abdeen;1st, Salamah bin Salamah St Albunayat, Amman 11623 Jordan
//    1151;Fluidyne Control Systems Pvt. Ltd;Shubhankar Menon;S.No. 79/2, Plot No. 12 Near Agarwal Godown, Shivane Pune, Maharashtra 411023 India
//    1152;Prism Systems, Inc.;David McClurg;200 Virginia St. Mobile, AL 36603 USA
//    1153;Enertiv;Sharad Shankar;320 W. 37th Street 15th Floor New York, NY 10018 USA
//    1154;Mirasoft GmbH & Co. KG;Thomas Hepp;Steingraben 13 Neuendorf, 97788 Germany
//    1155;DUALTECH IT;Anders Johansson;Gruvgatan 6 , 421 30 V. Frölunda Sweden
//    1156;Countlogic, LLC;Thomas Aiken;22 Church Street Liberty Corner, NJ 07938 USA
//    1157;Kohler;Nicole Dierksheide;444 Highland Drive Mailstop 072 Kohler, WI 53044 USA
//    1158;Chen Sen Controls Co., Ltd.;Mei Rui;Room 1203 Tianhui Mansion 26 Huaxing Road, Longgang District Shenzhen, 518116 China
//    1159;Greenheck;Robert Kraft;400 Ross Avenue Schofield, WI 54476 USA
//    1160;Intwine Connect, LLC;Ryan May;8401 Chagrin Road, Suite 10A Chagrin Falls, OH 44023 USA
//    1161;Karlborgs Elkontroll;Alexander Schmidt;Sävelundsgatan 8 Alingsås, SE-441 38 Sweden
//    1162;Datakom;Mehmet Hekimoglu;Serifali Mahallesi, Bayraktar Bulvari Kutup Sokak No: 26 34775 Umraniye/Istanbul Turkey
//    1163;Hoga Control AS;Kato Gangstad;Hollingsbukta 19 N-6409 Molde Norway
//    1164;Cool Automation;Kholodenko Alexander;2 Mivtahim Street Petah Tikva, Israel
//    1165;Inter Search Co., Ltd;Atsushi Machida;306, 14-23 Wakitahoncho Kawagoe-shi Saitama, 350-1123 Japan
//    1166;DABBEL-Automation Intelligence GmbH;Abdel Samaniego;Völklinger Str. 4 , 40219 Düsseldorf Germany
//    1167;Gadgeon Engineering Smartness;Hariprasad Nair;V1 405/E1, Fathima Tower Thrikkakara PO Kakkanad, Kochi-682021 India
//    1168;Coster Group S.r.l.;David Manca;via San Giovanni Battista de la Salle 4/A Milano, 20132 Italy
//    1169;Walter Müller AG;Ruedi Daetwyler;Russikerstrasse 37 , 8320 Fehraltorf Switzerland
//    1170;Fluke;Erik Nijhuis;P O Box 9090 Everett, WA 98206 USA
//    1171;Quintex Systems Ltd;Ben Wyatt;8 Ivanhoe Road Finchampstead, Reading RG40 4QQ United Kingdom
//    1172;Senfficient SDN BHD;Lim Choon Nyak;No. 34, Jalan TS 6/7, Taman Industri Subang Subang Jaya, Selangor 47510 Malaysia
//    1173;Nube iO Operations Pty Ltd;Jo Pritt;PO Box 81 Helensburgh, NSW 2508 Australia
//    1174;DAS Integrator Pte Ltd;Lim Thian;1 CleanTech Loop #02-04 CleanTech One Singapore, 637141 Singapore
//    1175;CREVIS Co., Ltd;Youngdong Song;29-4, Gigok-ro, Giheung-gu Yongin-si, Gyeonggi-do 17099 Korea
//    1176;iSquared software inc.;Stefan Roibu;224 Rue du Sud, CP-162 Cowansville, QC J2K-3H6 Canada
//    1177;KTG GmbH;Thomas Gindl;Marschnerstr. 18 Munich, 81245 Germany
//    1178;POK Group Oy;Jarkko Hautakorpi;Ketotie 4 Kuopio, 70700 Finland
//    1179;Adiscom;Christophe Le Verdier;288 boulevard Theophile Sueur 93100 Montreuil France
//    1180;Incusense;Kurt Kostrba;22000 SE Matthews Creek Lane Amity, OR 97101 USA
//    1181;75F;Gaurav Burman;1564, Revanna's, 1st Floor, 27th Main Rd 2nd Sector, HSR Layout Bengaluru, Kamataka 560102 India
//    1182;Anord Mardix, Inc.;Robert Lantzy;3930 Technology Court Sandston, VA 23150 USA
//    1183;HOSCH Gebäudeautomation Neue Produkte GmbH;Armin Althaus;Rheinstraße 9 D-14513 Teltow Germany
//    1184;Bosch.IO GmbH;Daniel Janev;88090 Immenstaad Germany
//    1185;Royal Boon Edam International B.V.;Martin Moes;Ambachtstraat 4 1135 GG Edam Netherlands
//    1186;Clack Corporation;David Peters;4462 Duraform Lane Windsor, WI 53598 USA
//    1187;Unitex Controls LLC;Firas Obeido;PO Box 1996 Amman, 11821 Jordan
//    1188;KTC Göteborg AB;Kenneth Strid;Datavägen 14A S-436 32 Askim Sweden
//    1189;Interzon AB;Mike Lindfors;Propellervägen 4A Täby, SE-183-62 Stockholm Sweden
//    1190;ISDE ING SL;Jorge Bueno;Ciudad de Frias 21 Nave 3 Madrid, PS 28021 Spain
//    1191;ABM automation building messaging GmbH;Markus Beißer;Ennserstraße 83 , 4407 Dietach Austria
//    1192;Kentec Electronics Ltd;Philip Barton;Unit 25-27 Fawkes Avenue Questor Dartford, Kent DA1 1JQ United Kingdom
//    1193;Emerson Commercial and Residential Solutions;Joe Stickels;1065 Big Shanty Road, NW Suite 110 Kennesaw, GA 30144 USA
//    1194;Powerside;Ian Smith;980 Atlantic Ave. Alameda, CA 94501 USA
//    1195;SMC Group;Nick Deabill;SM House, School Close Chandlers Ford Ind. Est. Eastleigh, Hampshire SO534BY United Kingdom
//    1196;EOS Weather Instruments;Henk van Heuveln;110 Chain Lake Drive Unit 3A Halifax, NS B3S 1A7 Canada
//    1197;Zonex Systems;Jeff Osheroff;5622 Engineer Drive Huntington Beach, CA 92649 USA
//    1198;Generex Systems Computervertriebsgesellschaft mbH;Frank Blettenberger;Brunnenkoppel 3 Hamburg, 22041 Germany
//    1199;Energy Wall LLC;David Eplee;1002 New Holland Ave Lancaster, PA 17601 USA
//    1200;Thermofin;Karolin Hemp;Am Windrad 1 Heinsdorfergrund, 08468 Germany
//    1201;SDATAWAY SA;Jordan Hadjedz;Route de Montreux, 149 1618 Châtel-Saint-Denis Switzerland
//    1202;Biddle Air Systems Limited;Michael Hims;St. Marys Road, Nuneaton Warwickshire, CV11 5AU United Kingdom
//    1203;Kessler Ellis Products;Emil Del Prete;10 Industrial Way East, Suite 109 Eatontown, NJ 07724 USA
//    1204;Thermoscreens;Ram Marwaha;St. Marys Road Nuneaton Warwickshire, CV11 5AU United Kingdom
//    1205;Modio;Fredrik von Hofsten;S:t Larsgatan 15 Linköping, 58224 Sweden
//    1206;Newron Solutions;Srinivas Bijili;Plot No 32, Eswaraiah Enclave Dammaiguda Hyderabad, Telangana 500083 India
//    1207;Unitronics;Amit Harari;PO Box 300, Ben Gurion Airport 7019900 Israel
//    1208;TRILUX GmbH & Co. KG;Michael Spall;Heidestrasse 4 D-59759, Amsberg Germany
//    1209;Kollmorgen Steuerungstechnik GmbH;Stefan Beck;Broichstraße 32 51109-Köln, Germany
//    1210;Bosch Rexroth AG;Philipp Guth;Bürgermeister-Dr.-Nebel-Straße 2 97816 Lohr a. Main, Germany
//    1211;Alarko Carrier;Murat Copur;Muallim Naci Cad. No: 69 34347 Ortaköy, Istanbul Turkey
//    1212;Verdigris Technologies;Jonathan Chu;NASA AMES Research Center Building 19, Room 1077 Moffett Field, CA 94035 USA
//    1213;Shanghai SIIC-Longchuang Smarter Energy Technology Co., Ltd.;Chris Tang;3/5F, Building A, Information Service Industry Base No. 1188 WanRong Road Shanghai, 200436 China
//    1214;Quinda Co.;Duffy O'Craven;1600 Beacon St. #806 Brookline, MA 02446 USA
//    1215;GRUNER AG;Wolfgang Spreitzer;Buerglestr. 15-17 78564 Wehingen Germany
//    1216;BACMOVE;Louis Perreault;Quebec, Quebec G1M 3P7 Canada
//    1217;PSIDAC AB;Bjorn Österlund;Bodarnevägen 37 , 82532 Iggesund Sweden
//    1218;ISICON-Control Automation;Sergio Ferrari;Nogoya 3880 CABA, Buenos Aires CP 1417 Argentina
//    1219;Big Ass Fans;Pete Maley;2348 Innovation Dr. Lexington, KY 40511 USA
//    1220;din - Dietmar Nocker Facility Management GmbH;Jürgen Galler;Kotzinastraße 5-7 4030-Linz Austria
//    1221;Teldio;Mark Dabrowski;390 March Rd Suite 110 Kanata, ON K2K 0G7 Canada
//    1222;MIKROKLIMA s.r.o.;Meduna Jaroslav;Palenecka 158 / 58z 500 04 Hradec Kralove Czech Republic
//    1223;Density;Sylvia Zabycz;235 Harrison St. Syracuse, NY 13202 USA
//    1224;ICONAG-Leittechnik GmbH;Harald Puhl;Vollmersbachstraße 88 D-55743 Idar-Oberstein Germany
//    1225;Awair;Dean Young;40 Boardman Place San Francisco, CA 94103 USA
//    1226;T&D Engineering, Ltd;Le Hieu;30 Nguyen Khanh Toan Cau Giay, Hanoi 122480 Vietnam
//    1227;Sistemas Digitales;Jorge Gaete;Av. Las Perdices 2970 C.21 Peñalolén, Santiago Chile
//    1228;Loxone Electronics GmbH;Rüdiger Keinberger;Smart Home 1 4154 Kollerschlag Austria
//    1229;ActronAir;Eric O'Donnell;7 Fairview Place Marsden Park NSW 2765 Australia
//    1230;Inductive Automation;Katharina Robinett;90 Blue Ravine Folsom, CA 95630 USA
//    1231;Thor Engineering GmbH;Roy Schneider;Koblenzer Straße 96 53177 Bonn Germany
//    1232;Berner International, LLC;Denise Grady;111 Progress Avenue New Castle, PA 16101 USA
//    1233;Potsdam Sensors LLC;Suresh Dhaniyala;65 Main St, Suite 3102 Potsdam, NY 13676-4039 USA
//    1234;Kohler Mira Ltd;Rich Ellis;Cromwell Road Cheltenham Gloucestershire GL52 5EP United Kingdom
//    1235;;Tobias Löbner;Mühlhaldenstr. 25 Denkendorf Baden-Württemberg 73770 Germany
//    1236;;Frederick G Kaestner, Jr.;7311 Hwy 329, Unit 910 Crestwood, KY 40014 USA
//    1237;;Philip Loh;6 Harper Road #06-02 369674 Singapore
//    1238;EATON CEAG Notlichtsysteme GmbH;Thomas Degen;Senator-Schwartz-Ring 26 Soest 59494 Germany
//    1239;Commbox Tecnologia;David Santi;R. Cel. Armando Assis 222 Três Figueiras, Porto Alegre-RS CEP 913300-010 Brazil
//    1240;IPVideo Corporation;Frank Jacovino;1490 North Clinton Avenue Bay Shore, New York 11706 USA
//    1241;Bender GmbH & Co. KG;Frank Baier;Londorfer Str. 65, Grünberg, 35305 Germany
//    1242;Rhymebus Corporation;Sam Chang;No. 17, 33rd Road, Taichung Industrial Park Taichung 40768 Taiwan
//    1243;Axon Systems Ltd;Anthony Duffy;16 Westcroft Sunderland SR6 7BP United Kingdom
//    1244;Engineered Air;Matt McRae;1401 Hastings Cres. SE Calgary, AB T2G 4C8 Canada
//    1245;Elipse Software Ltda;Alexandre Corrêa;R. Mostardeiro 322, Room 902 Porto Alegre, RS 90430-000 Brazil
//    1246;Simatix Building Technologies Pvt. Ltd.;Moose Mubaraque;Indiranagar Bangalore â€“ 560038 India
//    1247;W.A. Benjamin Electric Co.;Larry Dew;1615 Staunton Avenue Los Angeles, CA 90021 USA
//    1248;TROX Air Conditioning Components (Suzhou) Co. Ltd.;Jiangting Zhuang;Block 46 Chuangtou Industrial Workshop Yangchenghu Avenue, Loufeng North Park, SIP China
//    1249;SC Medical Pty Ltd.;Nicholas Ullrich;Unit 10, 12 â€“ 18 Clarendon Street Artarmon NSW 2250 Australia
//    1250;Elcanic A/S;Niels Andersen;Gørtlervej 5 Dk-5750 Ringe Denmark
    
    // @formatter:on

    private int code;

    private String name;

    private VendorType(final int code, final String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
