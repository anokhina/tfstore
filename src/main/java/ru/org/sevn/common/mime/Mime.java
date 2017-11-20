/*
 * Copyright 2017 Veronica Anokhina.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ru.org.sevn.common.mime;

//https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types/Complete_list_of_MIME_types

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Mime {

    /*
.aac	AAC audio file	audio/aac
.abw	AbiWord document	application/x-abiword
.arc	Archive document (multiple files embedded)	application/octet-stream
.avi	AVI: Audio Video Interleave	video/x-msvideo
.azw	Amazon Kindle eBook format	application/vnd.amazon.ebook
.bin	Any kind of binary data	application/octet-stream
.bz	BZip archive	application/x-bzip
.bz2	BZip2 archive	application/x-bzip2
.csh	C-Shell script	application/x-csh
.css	Cascading Style Sheets (CSS)	text/css
.csv	Comma-separated values (CSV)	text/csv
.doc	Microsoft Word	application/msword
.epub	Electronic publication (EPUB)	application/epub+zip
.gif	Graphics Interchange Format (GIF)	image/gif
.htm
.html	HyperText Markup Language (HTML)	text/html
.ico	Icon format	image/x-icon
.ics	iCalendar format	text/calendar
.jar	Java Archive (JAR)	application/java-archive
.jpeg
.jpg	JPEG images	image/jpeg
.js	JavaScript (ECMAScript)	application/javascript
.json	JSON format	application/json
.mid
.midi	Musical Instrument Digital Interface (MIDI)	audio/midi
.mpeg	MPEG Video	video/mpeg
.mpkg	Apple Installer Package	application/vnd.apple.installer+xml
.odp	OpenDocuemnt presentation document	application/vnd.oasis.opendocument.presentation
.ods	OpenDocuemnt spreadsheet document	application/vnd.oasis.opendocument.spreadsheet
.odt	OpenDocument text document	application/vnd.oasis.opendocument.text
.oga	OGG audio	audio/ogg
.ogv	OGG video	video/ogg
.ogx	OGG	application/ogg
.pdf	Adobe Portable Document Format (PDF)	application/pdf
.ppt	Microsoft PowerPoint	application/vnd.ms-powerpoint
.rar	RAR archive	application/x-rar-compressed
.rtf	Rich Text Format (RTF)	application/rtf
.sh	Bourne shell script	application/x-sh
.svg	Scalable Vector Graphics (SVG)	image/svg+xml
.swf	Small web format (SWF) or Adobe Flash document	application/x-shockwave-flash
.tar	Tape Archive (TAR)	application/x-tar
.tif
.tiff	Tagged Image File Format (TIFF)	image/tiff
.ttf	TrueType Font	font/ttf
.vsd	Microsoft Visio	application/vnd.visio
.wav	Waveform Audio Format	audio/x-wav
.weba	WEBM audio	audio/webm
.webm	WEBM video	video/webm
.webp	WEBP image	image/webp
.woff	Web Open Font Format (WOFF)	font/woff
.woff2	Web Open Font Format (WOFF)	font/woff2
.xhtml	XHTML	application/xhtml+xml
.xls	Microsoft Excel	application/vnd.ms-excel
.xml	XML	application/xml
.xul	XUL	application/vnd.mozilla.xul+xml
.zip	ZIP archive	application/zip
.3gp	3GPP audio/video container	video/3gpp
audio/3gpp if it doesn't contain video
.3g2	3GPP2 audio/video container	video/3gpp2
audio/3gpp2 if it doesn't contain video
.7z	7-zip archive	application/x-7z-compressed
    
    */
    
    private static final HashMap<String, String> mimeMap = new HashMap<>();
    
    static {
        String str = ".aw application/applixware\n" +
".atom application/atom+xml\n" +
".atomcat application/atomcat+xml\n" +
".atomsvc application/atomsvc+xml\n" +
".ccxml application/ccxml+xml\n" +
".cdmia application/cdmi-capability\n" +
".cdmic application/cdmi-container\n" +
".cdmid application/cdmi-domain\n" +
".cdmio application/cdmi-object\n" +
".cdmiq application/cdmi-queue\n" +
".cu application/cu-seeme\n" +
".davmount application/davmount+xml\n" +
".dssc application/dssc+der\n" +
".xdssc application/dssc+xml\n" +
".es application/ecmascript\n" +
".emma application/emma+xml\n" +
".epub application/epub+zip\n" +
".exi application/exi\n" +
".pfr application/font-tdpfr\n" +
".stk application/hyperstudio\n" +
".ipfix application/ipfix\n" +
".jar application/java-archive\n" +
".ser application/java-serialized-object\n" +
".class application/java-vm\n" +
".js application/javascript\n" +
".json application/json\n" +
".hqx application/mac-binhex40\n" +
".cpt application/mac-compactpro\n" +
".mads application/mads+xml\n" +
".mrc application/marc\n" +
".mrcx application/marcxml+xml\n" +
".ma application/mathematica\n" +
".mathml application/mathml+xml\n" +
".mbox application/mbox\n" +
".mscml application/mediaservercontrol+xml\n" +
".meta4 application/metalink4+xml\n" +
".mets application/mets+xml\n" +
".mods application/mods+xml\n" +
".m21 application/mp21\n" +
".mp4 application/mp4\n" +
".doc application/msword\n" +
".mxf application/mxf\n" +
".bin application/octet-stream\n" +
".oda application/oda\n" +
".opf application/oebps-package+xml\n" +
".ogx application/ogg\n" +
".onetoc application/onenote\n" +
".xer application/patch-ops-error+xml\n" +
".pdf application/pdf\n" +
".pgp application/pgp-encrypted\n" +
".pgp application/pgp-signature\n" +
".prf application/pics-rules\n" +
".p10 application/pkcs10\n" +
".p7m application/pkcs7-mime\n" +
".p7s application/pkcs7-signature\n" +
".p8 application/pkcs8\n" +
".ac application/pkix-attr-cert\n" +
".cer application/pkix-cert\n" +
".crl application/pkix-crl\n" +
".pkipath application/pkix-pkipath\n" +
".pki application/pkixcmp\n" +
".pls application/pls+xml\n" +
".ai application/postscript\n" +
".cww application/prs.cww\n" +
".pskcxml application/pskc+xml\n" +
".rdf application/rdf+xml\n" +
".rif application/reginfo+xml\n" +
".rnc application/relax-ng-compact-syntax\n" +
".rl application/resource-lists+xml\n" +
".rld application/resource-lists-diff+xml\n" +
".rs application/rls-services+xml\n" +
".rsd application/rsd+xml\n" +
".rss application/rss+xml\n" +
".rtf application/rtf\n" +
".sbml application/sbml+xml\n" +
".scq application/scvp-cv-request\n" +
".scs application/scvp-cv-response\n" +
".spq application/scvp-vp-request\n" +
".spp application/scvp-vp-response\n" +
".sdp application/sdp\n" +
".setpay application/set-payment-initiation\n" +
".setreg application/set-registration-initiation\n" +
".shf application/shf+xml\n" +
".smi application/smil+xml\n" +
".rq application/sparql-query\n" +
".srx application/sparql-results+xml\n" +
".gram application/srgs\n" +
".grxml application/srgs+xml\n" +
".sru application/sru+xml\n" +
".ssml application/ssml+xml\n" +
".tei application/tei+xml\n" +
".tfi application/thraud+xml\n" +
".tsd application/timestamped-data\n" +
".plb application/vnd.3gpp.pic-bw-large\n" +
".psb application/vnd.3gpp.pic-bw-small\n" +
".pvb application/vnd.3gpp.pic-bw-var\n" +
".tcap application/vnd.3gpp2.tcap\n" +
".pwn application/vnd.3m.post-it-notes\n" +
".aso application/vnd.accpac.simply.aso\n" +
".imp application/vnd.accpac.simply.imp\n" +
".acu application/vnd.acucobol\n" +
".atc application/vnd.acucorp\n" +
".air application/vnd.adobe.air-application-installer-package+zip\n" +
".fxp application/vnd.adobe.fxp\n" +
".xdp application/vnd.adobe.xdp+xml\n" +
".xfdf application/vnd.adobe.xfdf\n" +
".ahead application/vnd.ahead.space\n" +
".azf application/vnd.airzip.filesecure.azf\n" +
".azs application/vnd.airzip.filesecure.azs\n" +
".azw application/vnd.amazon.ebook\n" +
".acc application/vnd.americandynamics.acc\n" +
".ami application/vnd.amiga.ami\n" +
".apk application/vnd.android.package-archive\n" +
".cii application/vnd.anser-web-certificate-issue-initiation\n" +
".fti application/vnd.anser-web-funds-transfer-initiation\n" +
".atx application/vnd.antix.game-component\n" +
".mpkg application/vnd.apple.installer+xml\n" +
".m3u8 application/vnd.apple.mpegurl\n" +
".swi application/vnd.aristanetworks.swi\n" +
".aep application/vnd.audiograph\n" +
".mpm application/vnd.blueice.multipass\n" +
".bmi application/vnd.bmi\n" +
".rep application/vnd.businessobjects\n" +
".cdxml application/vnd.chemdraw+xml\n" +
".mmd application/vnd.chipnuts.karaoke-mmd\n" +
".cdy application/vnd.cinderella\n" +
".cla application/vnd.claymore\n" +
".rp9 application/vnd.cloanto.rp9\n" +
".c4g application/vnd.clonk.c4group\n" +
".c11amc application/vnd.cluetrust.cartomobile-config\n" +
".c11amz application/vnd.cluetrust.cartomobile-config-pkg\n" +
".csp application/vnd.commonspace\n" +
".cdbcmsg application/vnd.contact.cmsg\n" +
".cmc application/vnd.cosmocaller\n" +
".clkx application/vnd.crick.clicker\n" +
".clkk application/vnd.crick.clicker.keyboard\n" +
".clkp application/vnd.crick.clicker.palette\n" +
".clkt application/vnd.crick.clicker.template\n" +
".clkw application/vnd.crick.clicker.wordbank\n" +
".wbs application/vnd.criticaltools.wbs+xml\n" +
".pml application/vnd.ctc-posml\n" +
".ppd application/vnd.cups-ppd\n" +
".car application/vnd.curl.car\n" +
".pcurl application/vnd.curl.pcurl\n" +
".rdz application/vnd.data-vision.rdz\n" +
".fe_launch application/vnd.denovo.fcselayout-link\n" +
".dna application/vnd.dna\n" +
".mlp application/vnd.dolby.mlp\n" +
".dpg application/vnd.dpgraph\n" +
".dfac application/vnd.dreamfactory\n" +
".ait application/vnd.dvb.ait\n" +
".svc application/vnd.dvb.service\n" +
".geo application/vnd.dynageo\n" +
".mag application/vnd.ecowin.chart\n" +
".nml application/vnd.enliven\n" +
".esf application/vnd.epson.esf\n" +
".msf application/vnd.epson.msf\n" +
".qam application/vnd.epson.quickanime\n" +
".slt application/vnd.epson.salt\n" +
".ssf application/vnd.epson.ssf\n" +
".es3 application/vnd.eszigno3+xml\n" +
".ez2 application/vnd.ezpix-album\n" +
".ez3 application/vnd.ezpix-package\n" +
".fdf application/vnd.fdf\n" +
".seed application/vnd.fdsn.seed\n" +
".gph application/vnd.flographit\n" +
".ftc application/vnd.fluxtime.clip\n" +
".fm application/vnd.framemaker\n" +
".fnc application/vnd.frogans.fnc\n" +
".ltf application/vnd.frogans.ltf\n" +
".fsc application/vnd.fsc.weblaunch\n" +
".oas application/vnd.fujitsu.oasys\n" +
".oa2 application/vnd.fujitsu.oasys2\n" +
".oa3 application/vnd.fujitsu.oasys3\n" +
".fg5 application/vnd.fujitsu.oasysgp\n" +
".bh2 application/vnd.fujitsu.oasysprs\n" +
".ddd application/vnd.fujixerox.ddd\n" +
".xdw application/vnd.fujixerox.docuworks\n" +
".xbd application/vnd.fujixerox.docuworks.binder\n" +
".fzs application/vnd.fuzzysheet\n" +
".txd application/vnd.genomatix.tuxedo\n" +
".ggb application/vnd.geogebra.file\n" +
".ggt application/vnd.geogebra.tool\n" +
".gex application/vnd.geometry-explorer\n" +
".gxt application/vnd.geonext\n" +
".g2w application/vnd.geoplan\n" +
".g3w application/vnd.geospace\n" +
".gmx application/vnd.gmx\n" +
".kml application/vnd.google-earth.kml+xml\n" +
".kmz application/vnd.google-earth.kmz\n" +
".gqf application/vnd.grafeq\n" +
".gac application/vnd.groove-account\n" +
".ghf application/vnd.groove-help\n" +
".gim application/vnd.groove-identity-message\n" +
".grv application/vnd.groove-injector\n" +
".gtm application/vnd.groove-tool-message\n" +
".tpl application/vnd.groove-tool-template\n" +
".vcg application/vnd.groove-vcard\n" +
".hal application/vnd.hal+xml\n" +
".zmm application/vnd.handheld-entertainment+xml\n" +
".hbci application/vnd.hbci\n" +
".les application/vnd.hhe.lesson-player\n" +
".hpgl application/vnd.hp-hpgl\n" +
".hpid application/vnd.hp-hpid\n" +
".hps application/vnd.hp-hps\n" +
".jlt application/vnd.hp-jlyt\n" +
".pcl application/vnd.hp-pcl\n" +
".pclxl application/vnd.hp-pclxl\n" +
".sfd-hdstx application/vnd.hydrostatix.sof-data\n" +
".x3d application/vnd.hzn-3d-crossword\n" +
".mpy application/vnd.ibm.minipay\n" +
".afp application/vnd.ibm.modcap\n" +
".irm application/vnd.ibm.rights-management\n" +
".sc application/vnd.ibm.secure-container\n" +
".icc application/vnd.iccprofile\n" +
".igl application/vnd.igloader\n" +
".ivp application/vnd.immervision-ivp\n" +
".ivu application/vnd.immervision-ivu\n" +
".igm application/vnd.insors.igm\n" +
".xpw application/vnd.intercon.formnet\n" +
".i2g application/vnd.intergeo\n" +
".qbo application/vnd.intu.qbo\n" +
".qfx application/vnd.intu.qfx\n" +
".rcprofile application/vnd.ipunplugged.rcprofile\n" +
".irp application/vnd.irepository.package+xml\n" +
".xpr application/vnd.is-xpr\n" +
".fcs application/vnd.isac.fcs\n" +
".jam application/vnd.jam\n" +
".rms application/vnd.jcp.javame.midlet-rms\n" +
".jisp application/vnd.jisp\n" +
".joda application/vnd.joost.joda-archive\n" +
".ktz application/vnd.kahootz\n" +
".karbon application/vnd.kde.karbon\n" +
".chrt application/vnd.kde.kchart\n" +
".kfo application/vnd.kde.kformula\n" +
".flw application/vnd.kde.kivio\n" +
".kon application/vnd.kde.kontour\n" +
".kpr application/vnd.kde.kpresenter\n" +
".ksp application/vnd.kde.kspread\n" +
".kwd application/vnd.kde.kword\n" +
".htke application/vnd.kenameaapp\n" +
".kia application/vnd.kidspiration\n" +
".kne application/vnd.kinar\n" +
".skp application/vnd.koan\n" +
".sse application/vnd.kodak-descriptor\n" +
".lasxml application/vnd.las.las+xml\n" +
".lbd application/vnd.llamagraphics.life-balance.desktop\n" +
".lbe application/vnd.llamagraphics.life-balance.exchange+xml\n" +
".123 application/vnd.lotus-1-2-3\n" +
".apr application/vnd.lotus-approach\n" +
".pre application/vnd.lotus-freelance\n" +
".nsf application/vnd.lotus-notes\n" +
".org application/vnd.lotus-organizer\n" +
".scm application/vnd.lotus-screencam\n" +
".lwp application/vnd.lotus-wordpro\n" +
".portpkg application/vnd.macports.portpkg\n" +
".mcd application/vnd.mcd\n" +
".mc1 application/vnd.medcalcdata\n" +
".cdkey application/vnd.mediastation.cdkey\n" +
".mwf application/vnd.mfer\n" +
".mfm application/vnd.mfmp\n" +
".flo application/vnd.micrografx.flo\n" +
".igx application/vnd.micrografx.igx\n" +
".mif application/vnd.mif\n" +
".daf application/vnd.mobius.daf\n" +
".dis application/vnd.mobius.dis\n" +
".mbk application/vnd.mobius.mbk\n" +
".mqy application/vnd.mobius.mqy\n" +
".msl application/vnd.mobius.msl\n" +
".plc application/vnd.mobius.plc\n" +
".txf application/vnd.mobius.txf\n" +
".mpn application/vnd.mophun.application\n" +
".mpc application/vnd.mophun.certificate\n" +
".xul application/vnd.mozilla.xul+xml\n" +
".cil application/vnd.ms-artgalry\n" +
".cab application/vnd.ms-cab-compressed\n" +
".xls application/vnd.ms-excel\n" +
".xlam application/vnd.ms-excel.addin.macroenabled.12\n" +
".xlsb application/vnd.ms-excel.sheet.binary.macroenabled.12\n" +
".xlsm application/vnd.ms-excel.sheet.macroenabled.12\n" +
".xltm application/vnd.ms-excel.template.macroenabled.12\n" +
".eot application/vnd.ms-fontobject\n" +
".chm application/vnd.ms-htmlhelp\n" +
".ims application/vnd.ms-ims\n" +
".lrm application/vnd.ms-lrm\n" +
".thmx application/vnd.ms-officetheme\n" +
".cat application/vnd.ms-pki.seccat\n" +
".stl application/vnd.ms-pki.stl\n" +
".ppt application/vnd.ms-powerpoint\n" +
".ppam application/vnd.ms-powerpoint.addin.macroenabled.12\n" +
".pptm application/vnd.ms-powerpoint.presentation.macroenabled.12\n" +
".sldm application/vnd.ms-powerpoint.slide.macroenabled.12\n" +
".ppsm application/vnd.ms-powerpoint.slideshow.macroenabled.12\n" +
".potm application/vnd.ms-powerpoint.template.macroenabled.12\n" +
".mpp application/vnd.ms-project\n" +
".docm application/vnd.ms-word.document.macroenabled.12\n" +
".dotm application/vnd.ms-word.template.macroenabled.12\n" +
".wps application/vnd.ms-works\n" +
".wpl application/vnd.ms-wpl\n" +
".xps application/vnd.ms-xpsdocument\n" +
".mseq application/vnd.mseq\n" +
".mus application/vnd.musician\n" +
".msty application/vnd.muvee.style\n" +
".nlu application/vnd.neurolanguage.nlu\n" +
".nnd application/vnd.noblenet-directory\n" +
".nns application/vnd.noblenet-sealer\n" +
".nnw application/vnd.noblenet-web\n" +
".ngdat application/vnd.nokia.n-gage.data\n" +
".n-gage application/vnd.nokia.n-gage.symbian.install\n" +
".rpst application/vnd.nokia.radio-preset\n" +
".rpss application/vnd.nokia.radio-presets\n" +
".edm application/vnd.novadigm.edm\n" +
".edx application/vnd.novadigm.edx\n" +
".ext application/vnd.novadigm.ext\n" +
".odc application/vnd.oasis.opendocument.chart\n" +
".otc application/vnd.oasis.opendocument.chart-template\n" +
".odb application/vnd.oasis.opendocument.database\n" +
".odf application/vnd.oasis.opendocument.formula\n" +
".odft application/vnd.oasis.opendocument.formula-template\n" +
".odg application/vnd.oasis.opendocument.graphics\n" +
".otg application/vnd.oasis.opendocument.graphics-template\n" +
".odi application/vnd.oasis.opendocument.image\n" +
".oti application/vnd.oasis.opendocument.image-template\n" +
".odp application/vnd.oasis.opendocument.presentation\n" +
".otp application/vnd.oasis.opendocument.presentation-template\n" +
".ods application/vnd.oasis.opendocument.spreadsheet\n" +
".ots application/vnd.oasis.opendocument.spreadsheet-template\n" +
".odt application/vnd.oasis.opendocument.text\n" +
".odm application/vnd.oasis.opendocument.text-master\n" +
".ott application/vnd.oasis.opendocument.text-template\n" +
".oth application/vnd.oasis.opendocument.text-web\n" +
".xo application/vnd.olpc-sugar\n" +
".dd2 application/vnd.oma.dd2+xml\n" +
".oxt application/vnd.openofficeorg.extension\n" +
".pptx application/vnd.openxmlformats-officedocument.presentationml.presentation\n" +
".sldx application/vnd.openxmlformats-officedocument.presentationml.slide\n" +
".ppsx application/vnd.openxmlformats-officedocument.presentationml.slideshow\n" +
".potx application/vnd.openxmlformats-officedocument.presentationml.template\n" +
".xlsx application/vnd.openxmlformats-officedocument.spreadsheetml.sheet\n" +
".xltx application/vnd.openxmlformats-officedocument.spreadsheetml.template\n" +
".docx application/vnd.openxmlformats-officedocument.wordprocessingml.document\n" +
".dotx application/vnd.openxmlformats-officedocument.wordprocessingml.template\n" +
".mgp application/vnd.osgeo.mapguide.package\n" +
".dp application/vnd.osgi.dp\n" +
".pdb application/vnd.palm\n" +
".paw application/vnd.pawaafile\n" +
".str application/vnd.pg.format\n" +
".ei6 application/vnd.pg.osasli\n" +
".efif application/vnd.picsel\n" +
".wg application/vnd.pmi.widget\n" +
".plf application/vnd.pocketlearn\n" +
".pbd application/vnd.powerbuilder6\n" +
".box application/vnd.previewsystems.box\n" +
".mgz application/vnd.proteus.magazine\n" +
".qps application/vnd.publishare-delta-tree\n" +
".ptid application/vnd.pvi.ptid1\n" +
".qxd application/vnd.quark.quarkxpress\n" +
".bed application/vnd.realvnc.bed\n" +
".mxl application/vnd.recordare.musicxml\n" +
".musicxml application/vnd.recordare.musicxml+xml\n" +
".cryptonote application/vnd.rig.cryptonote\n" +
".cod application/vnd.rim.cod\n" +
".rm application/vnd.rn-realmedia\n" +
".link66 application/vnd.route66.link66+xml\n" +
".st application/vnd.sailingtracker.track\n" +
".see application/vnd.seemail\n" +
".sema application/vnd.sema\n" +
".semd application/vnd.semd\n" +
".semf application/vnd.semf\n" +
".ifm application/vnd.shana.informed.formdata\n" +
".itp application/vnd.shana.informed.formtemplate\n" +
".iif application/vnd.shana.informed.interchange\n" +
".ipk application/vnd.shana.informed.package\n" +
".twd application/vnd.simtech-mindmapper\n" +
".mmf application/vnd.smaf\n" +
".teacher application/vnd.smart.teacher\n" +
".sdkm application/vnd.solent.sdkm+xml\n" +
".dxp application/vnd.spotfire.dxp\n" +
".sfs application/vnd.spotfire.sfs\n" +
".sdc application/vnd.stardivision.calc\n" +
".sda application/vnd.stardivision.draw\n" +
".sdd application/vnd.stardivision.impress\n" +
".smf application/vnd.stardivision.math\n" +
".sdw application/vnd.stardivision.writer\n" +
".sgl application/vnd.stardivision.writer-global\n" +
".sm application/vnd.stepmania.stepchart\n" +
".sxc application/vnd.sun.xml.calc\n" +
".stc application/vnd.sun.xml.calc.template\n" +
".sxd application/vnd.sun.xml.draw\n" +
".std application/vnd.sun.xml.draw.template\n" +
".sxi application/vnd.sun.xml.impress\n" +
".sti application/vnd.sun.xml.impress.template\n" +
".sxm application/vnd.sun.xml.math\n" +
".sxw application/vnd.sun.xml.writer\n" +
".sxg application/vnd.sun.xml.writer.global\n" +
".stw application/vnd.sun.xml.writer.template\n" +
".sus application/vnd.sus-calendar\n" +
".svd application/vnd.svd\n" +
".sis application/vnd.symbian.install\n" +
".xsm application/vnd.syncml+xml\n" +
".bdm application/vnd.syncml.dm+wbxml\n" +
".xdm application/vnd.syncml.dm+xml\n" +
".tao application/vnd.tao.intent-module-archive\n" +
".tmo application/vnd.tmobile-livetv\n" +
".tpt application/vnd.trid.tpt\n" +
".mxs application/vnd.triscape.mxs\n" +
".tra application/vnd.trueapp\n" +
".ufd application/vnd.ufdl\n" +
".utz application/vnd.uiq.theme\n" +
".umj application/vnd.umajin\n" +
".unityweb application/vnd.unity\n" +
".uoml application/vnd.uoml+xml\n" +
".vcx application/vnd.vcx\n" +
".vsd application/vnd.visio\n" +
".vsdx application/vnd.visio2013\n" +
".vis application/vnd.visionary\n" +
".vsf application/vnd.vsf\n" +
".wbxml application/vnd.wap.wbxml\n" +
".wmlc application/vnd.wap.wmlc\n" +
".wmlsc application/vnd.wap.wmlscriptc\n" +
".wtb application/vnd.webturbo\n" +
".nbp application/vnd.wolfram.player\n" +
".wpd application/vnd.wordperfect\n" +
".wqd application/vnd.wqd\n" +
".stf application/vnd.wt.stf\n" +
".xar application/vnd.xara\n" +
".xfdl application/vnd.xfdl\n" +
".hvd application/vnd.yamaha.hv-dic\n" +
".hvs application/vnd.yamaha.hv-script\n" +
".hvp application/vnd.yamaha.hv-voice\n" +
".osf application/vnd.yamaha.openscoreformat\n" +
".osfpvg application/vnd.yamaha.openscoreformat.osfpvg+xml\n" +
".saf application/vnd.yamaha.smaf-audio\n" +
".spf application/vnd.yamaha.smaf-phrase\n" +
".cmp application/vnd.yellowriver-custom-menu\n" +
".zir application/vnd.zul\n" +
".zaz application/vnd.zzazz.deck+xml\n" +
".vxml application/voicexml+xml\n" +
".wgt application/widget\n" +
".hlp application/winhlp\n" +
".wsdl application/wsdl+xml\n" +
".wspolicy application/wspolicy+xml\n" +
".7z application/x-7z-compressed\n" +
".abw application/x-abiword\n" +
".ace application/x-ace-compressed\n" +
".aab application/x-authorware-bin\n" +
".aam application/x-authorware-map\n" +
".aas application/x-authorware-seg\n" +
".bcpio application/x-bcpio\n" +
".torrent application/x-bittorrent\n" +
".bz application/x-bzip\n" +
".bz2 application/x-bzip2\n" +
".vcd application/x-cdlink\n" +
".chat application/x-chat\n" +
".pgn application/x-chess-pgn\n" +
".cpio application/x-cpio\n" +
".csh application/x-csh\n" +
".deb application/x-debian-package\n" +
".dir application/x-director\n" +
".wad application/x-doom\n" +
".ncx application/x-dtbncx+xml\n" +
".dtb application/x-dtbook+xml\n" +
".res application/x-dtbresource+xml\n" +
".dvi application/x-dvi\n" +
".bdf application/x-font-bdf\n" +
".gsf application/x-font-ghostscript\n" +
".psf application/x-font-linux-psf\n" +
".otf application/x-font-otf\n" +
".pcf application/x-font-pcf\n" +
".snf application/x-font-snf\n" +
".ttf application/x-font-ttf\n" +
".pfa application/x-font-type1\n" +
".woff application/x-font-woff\n" +
".spl application/x-futuresplash\n" +
".gnumeric application/x-gnumeric\n" +
".gtar application/x-gtar\n" +
".hdf application/x-hdf\n" +
".jnlp application/x-java-jnlp-file\n" +
".latex application/x-latex\n" +
".prc application/x-mobipocket-ebook\n" +
".application application/x-ms-application\n" +
".wmd application/x-ms-wmd\n" +
".wmz application/x-ms-wmz\n" +
".xbap application/x-ms-xbap\n" +
".mdb application/x-msaccess\n" +
".obd application/x-msbinder\n" +
".crd application/x-mscardfile\n" +
".clp application/x-msclip\n" +
".exe application/x-msdownload\n" +
".mvb application/x-msmediaview\n" +
".wmf application/x-msmetafile\n" +
".mny application/x-msmoney\n" +
".pub application/x-mspublisher\n" +
".scd application/x-msschedule\n" +
".trm application/x-msterminal\n" +
".wri application/x-mswrite\n" +
".nc application/x-netcdf\n" +
".p12 application/x-pkcs12\n" +
".p7b application/x-pkcs7-certificates\n" +
".p7r application/x-pkcs7-certreqresp\n" +
".rar application/x-rar-compressed\n" +
".sh application/x-sh\n" +
".shar application/x-shar\n" +
".swf application/x-shockwave-flash\n" +
".xap application/x-silverlight-app\n" +
".sit application/x-stuffit\n" +
".sitx application/x-stuffitx\n" +
".sv4cpio application/x-sv4cpio\n" +
".sv4crc application/x-sv4crc\n" +
".tar application/x-tar\n" +
".tcl application/x-tcl\n" +
".tex application/x-tex\n" +
".tfm application/x-tex-tfm\n" +
".texinfo application/x-texinfo\n" +
".ustar application/x-ustar\n" +
".src application/x-wais-source\n" +
".der application/x-x509-ca-cert\n" +
".fig application/x-xfig\n" +
".xpi application/x-xpinstall\n" +
".xdf application/xcap-diff+xml\n" +
".xenc application/xenc+xml\n" +
".xhtml application/xhtml+xml\n" +
".xml application/xml\n" +
".dtd application/xml-dtd\n" +
".xop application/xop+xml\n" +
".xslt application/xslt+xml\n" +
".xspf application/xspf+xml\n" +
".mxml application/xv+xml\n" +
".yang application/yang\n" +
".yin application/yin+xml\n" +
".zip application/zip\n" +
".adp audio/adpcm\n" +
".au audio/basic\n" +
".mid audio/midi\n" +
".mp4a audio/mp4\n" +
".mpga audio/mpeg\n" +
".oga audio/ogg\n" +
".uva audio/vnd.dece.audio\n" +
".eol audio/vnd.digital-winds\n" +
".dra audio/vnd.dra\n" +
".dts audio/vnd.dts\n" +
".dtshd audio/vnd.dts.hd\n" +
".lvp audio/vnd.lucent.voice\n" +
".pya audio/vnd.ms-playready.media.pya\n" +
".ecelp4800 audio/vnd.nuera.ecelp4800\n" +
".ecelp7470 audio/vnd.nuera.ecelp7470\n" +
".ecelp9600 audio/vnd.nuera.ecelp9600\n" +
".rip audio/vnd.rip\n" +
".weba audio/webm\n" +
".aac audio/x-aac\n" +
".aif audio/x-aiff\n" +
".m3u audio/x-mpegurl\n" +
".wax audio/x-ms-wax\n" +
".wma audio/x-ms-wma\n" +
".ram audio/x-pn-realaudio\n" +
".rmp audio/x-pn-realaudio-plugin\n" +
".wav audio/x-wav\n" +
".cdx chemical/x-cdx\n" +
".cif chemical/x-cif\n" +
".cmdf chemical/x-cmdf\n" +
".cml chemical/x-cml\n" +
".csml chemical/x-csml\n" +
".xyz chemical/x-xyz\n" +
".bmp image/bmp\n" +
".cgm image/cgm\n" +
".g3 image/g3fax\n" +
".gif image/gif\n" +
".ief image/ief\n" +
".jpeg image/jpeg\n" +
".jpg image/jpeg\n" +
".pjpeg image/pjpeg\n" +
".ktx image/ktx\n" +
".png image/png\n" +
".png image/x-png\n" +
".png image/x-citrix-png\n" +
".btif image/prs.btif\n" +
".svg image/svg+xml\n" +
".tiff image/tiff\n" +
".psd image/vnd.adobe.photoshop\n" +
".uvi image/vnd.dece.graphic\n" +
".sub image/vnd.dvb.subtitle\n" +
".djvu image/vnd.djvu\n" +
".dwg image/vnd.dwg\n" +
".dxf image/vnd.dxf\n" +
".fbs image/vnd.fastbidsheet\n" +
".fpx image/vnd.fpx\n" +
".fst image/vnd.fst\n" +
".mmr image/vnd.fujixerox.edmics-mmr\n" +
".rlc image/vnd.fujixerox.edmics-rlc\n" +
".mdi image/vnd.ms-modi\n" +
".npx image/vnd.net-fpx\n" +
".wbmp image/vnd.wap.wbmp\n" +
".xif image/vnd.xiff\n" +
".webp image/webp\n" +
".ras image/x-cmu-raster\n" +
".cmx image/x-cmx\n" +
".fh image/x-freehand\n" +
".ico image/x-icon\n" +
".pcx image/x-pcx\n" +
".pic image/x-pict\n" +
".pnm image/x-portable-anymap\n" +
".pbm image/x-portable-bitmap\n" +
".pgm image/x-portable-graymap\n" +
".ppm image/x-portable-pixmap\n" +
".rgb image/x-rgb\n" +
".xbm image/x-xbitmap\n" +
".xpm image/x-xpixmap\n" +
".xwd image/x-xwindowdump\n" +
".eml message/rfc822\n" +
".igs model/iges\n" +
".msh model/mesh\n" +
".dae model/vnd.collada+xml\n" +
".dwf model/vnd.dwf\n" +
".gdl model/vnd.gdl\n" +
".gtw model/vnd.gtw\n" +
".mts model/vnd.mts\n" +
".vtu model/vnd.vtu\n" +
".wrl model/vrml\n" +
".ics text/calendar\n" +
".css text/css\n" +
".csv text/csv\n" +
".html text/html\n" +
".n3 text/n3\n" +
".txt text/plain\n" +
".dsc text/prs.lines.tag\n" +
".rtx text/richtext\n" +
".sgml text/sgml\n" +
".tsv text/tab-separated-values\n" +
".t text/troff\n" +
".ttl text/turtle\n" +
".uri text/uri-list\n" +
".curl text/vnd.curl\n" +
".dcurl text/vnd.curl.dcurl\n" +
".scurl text/vnd.curl.scurl\n" +
".mcurl text/vnd.curl.mcurl\n" +
".fly text/vnd.fly\n" +
".flx text/vnd.fmi.flexstor\n" +
".gv text/vnd.graphviz\n" +
".3dml text/vnd.in3d.3dml\n" +
".spot text/vnd.in3d.spot\n" +
".jad text/vnd.sun.j2me.app-descriptor\n" +
".wml text/vnd.wap.wml\n" +
".wmls text/vnd.wap.wmlscript\n" +
".s text/x-asm\n" +
".c text/x-c\n" +
".f text/x-fortran\n" +
".p text/x-pascal\n" +
".java text/x-java-source\n" +
".etx text/x-setext\n" +
".uu text/x-uuencode\n" +
".vcs text/x-vcalendar\n" +
".vcf text/x-vcard\n" +
".3gp video/3gpp\n" +
".3g2 video/3gpp2\n" +
".h261 video/h261\n" +
".h263 video/h263\n" +
".h264 video/h264\n" +
".jpgv video/jpeg\n" +
".jpm video/jpm\n" +
".mj2 video/mj2\n" +
".mp4 video/mp4\n" +
".mpeg video/mpeg\n" +
".ogv video/ogg\n" +
".qt video/quicktime\n" +
".uvh video/vnd.dece.hd\n" +
".uvm video/vnd.dece.mobile\n" +
".uvp video/vnd.dece.pd\n" +
".uvs video/vnd.dece.sd\n" +
".uvv video/vnd.dece.video\n" +
".fvt video/vnd.fvt\n" +
".mxu video/vnd.mpegurl\n" +
".pyv video/vnd.ms-playready.media.pyv\n" +
".uvu video/vnd.uvvu.mp4\n" +
".viv video/vnd.vivo\n" +
".webm video/webm\n" +
".f4v video/x-f4v\n" +
".fli video/x-fli\n" +
".flv video/x-flv\n" +
".m4v video/x-m4v\n" +
".asf video/x-ms-asf\n" +
".wm video/x-ms-wm\n" +
".wmv video/x-ms-wmv\n" +
".wmx video/x-ms-wmx\n" +
".wvx video/x-ms-wvx\n" +
".avi video/x-msvideo\n" +
".movie video/x-sgi-movie\n" +
".ice x-conference/x-cooltalk\n" +
".par text/plain-bas\n" +
".yaml text/yaml\n" +
".dmg application/x-apple-diskimage";
        for (String line : str.split("\n")) {
            String aline[] = line.trim().split(" ");
            if (aline.length > 1) {
                mimeMap.put(aline[0], aline[1]);
                System.out.println("mime:" + aline[0] + ":" + aline[1]);
            }
        }
    }
    
    public static String getMimeTypeFile(java.io.File file) {
        String ret = getMimeTypeFile(file.getName());
        if (ret != null) return ret;
        try {
            return Files.probeContentType(file.toPath());
        } catch (IOException ex) {
            Logger.getLogger(Mime.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static String getMimeTypeFile(String fname) {
        int extIdx = fname.lastIndexOf(".");
        if (extIdx > 0) {
            return getMimeTypeExt(fname.substring(extIdx).toLowerCase());
        }
        return null;
    }

    public static boolean isTextDocument(String mime) {
        if (mime != null) {
            if (mime.startsWith("text")) return true;
            return TEXT_CT.contains(mime);
        }
        return false;
    }
    
    public static final HashSet<String> TEXT_CT = new HashSet<>(java.util.Arrays.asList(new String[] {
        "application/x-abiword", "application/pdf", "application/vnd.amazon.ebook",
        "application/msword", "application/epub+zip", "application/json",
        "application/vnd.visio", "application/xhtml+xml", "application/vnd.ms-excel",
        "application/xml", "application/vnd.mozilla.xul+xml", "application/vnd.oasis.opendocument.presentation", 
        "application/vnd.oasis.opendocument.spreadsheet", "application/vnd.oasis.opendocument.text"
    }));

    public static String getMimeTypeExt(String ext) {
        switch(ext) {
            case ".aac": /*  "AAC audio file"*/ return  "audio/aac";
            case ".abw": /*  "AbiWord document"*/ return  "application/x-abiword";
            case ".arc": /*  "Archive document (multiple files embedded)"*/ return  "application/octet-stream";
            case ".avi": /*  "AVI: Audio Video Interleave"*/ return  "video/x-msvideo";
            case ".azw": /*  "Amazon Kindle eBook format"*/ return  "application/vnd.amazon.ebook";
            case ".bin": /*  "Any kind of binary data"*/ return  "application/octet-stream";
            case ".bz": /*  "BZip archive"*/ return  "application/x-bzip";
            case ".bz2": /*  "BZip2 archive"*/ return  "application/x-bzip2";
            case ".csh": /*  "C-Shell script"*/ return  "application/x-csh";
            case ".css": /*  "Cascading Style Sheets (CSS)"*/ return  "text/css";
            case ".csv": /*  "Comma-separated values (CSV)"*/ return  "text/csv";
            case ".doc": /*  "Microsoft Word"*/ return  "application/msword";
            case ".epub": /*  "Electronic publication (EPUB)"*/ return  "application/epub+zip";
            case ".gif": /*  "Graphics Interchange Format (GIF)"*/ return  "image/gif";
            case ".html": /*  "HyperText Markup Language (HTML)"*/ return  "text/html";
            case ".htm": /*  "HyperText Markup Language (HTML)"*/ return  "text/html";
            case ".ico": /*  "Icon format"*/ return  "image/x-icon";
            case ".ics": /*  "iCalendar format"*/ return  "text/calendar";
            case ".jar": /*  "Java Archive (JAR)"*/ return  "application/java-archive";
            case ".jpg": /*  "JPEG images"*/ return  "image/jpeg";
            case ".jpeg": /*  "JPEG images"*/ return  "image/jpeg";
            case ".js": /*  "JavaScript (ECMAScript)"*/ return  "application/javascript";
            case ".json": /*  "JSON format"*/ return  "application/json";
            case ".mid": /*  "Musical Instrument Digital Interface (MIDI)"*/ return  "audio/midi";
            case ".midi": /*  "Musical Instrument Digital Interface (MIDI)"*/ return  "audio/midi";
            case ".mpeg": /*  "MPEG Video"*/ return  "video/mpeg";
            case ".mpkg": /*  "Apple Installer Package"*/ return  "application/vnd.apple.installer+xml";
            case ".odp": /*  "OpenDocuemnt presentation document"*/ return  "application/vnd.oasis.opendocument.presentation";
            case ".ods": /*  "OpenDocuemnt spreadsheet document"*/ return  "application/vnd.oasis.opendocument.spreadsheet";
            case ".odt": /*  "OpenDocument text document"*/ return  "application/vnd.oasis.opendocument.text";
            case ".oga": /*  "OGG audio"*/ return  "audio/ogg";
            case ".ogv": /*  "OGG video"*/ return  "video/ogg";
            case ".ogx": /*  "OGG"*/ return  "application/ogg";
            case ".pdf": /*  "Adobe Portable Document Format (PDF)"*/ return  "application/pdf";
            case ".ppt": /*  "Microsoft PowerPoint"*/ return  "application/vnd.ms-powerpoint";
            case ".rar": /*  "RAR archive"*/ return  "application/x-rar-compressed";
            case ".rtf": /*  "Rich Text Format (RTF)"*/ return  "application/rtf";
            case ".sh": /*  "Bourne shell script"*/ return  "application/x-sh";
            case ".svg": /*  "Scalable Vector Graphics (SVG)"*/ return  "image/svg+xml";
            case ".swf": /*  "Small web format (SWF) or Adobe Flash document"*/ return  "application/x-shockwave-flash";
            case ".tar": /*  "Tape Archive (TAR)"*/ return  "application/x-tar";
            case ".tif": /*  "Tagged Image File Format (TIFF)"*/ return  "image/tiff";
            case ".tiff": /*  "Tagged Image File Format (TIFF)"*/ return  "image/tiff";
            case ".ttf": /*  "TrueType Font"*/ return  "font/ttf";
            case ".txt": return  "text/plain";
            case ".text": return  "text/plain";
            case ".vsd": /*  "Microsoft Visio"*/ return  "application/vnd.visio";
            case ".wav": /*  "Waveform Audio Format"*/ return  "audio/x-wav";
            case ".weba": /*  "WEBM audio"*/ return  "audio/webm";
            case ".webm": /*  "WEBM video"*/ return  "video/webm";
            case ".webp": /*  "WEBP image"*/ return  "image/webp";
            case ".woff": /*  "Web Open Font Format (WOFF)"*/ return  "font/woff";
            case ".woff2": /*  "Web Open Font Format (WOFF)"*/ return  "font/woff2";
            case ".xhtml": /*  "XHTML"*/ return  "application/xhtml+xml";
            case ".xls": /*  "Microsoft Excel"*/ return  "application/vnd.ms-excel";
            case ".xml": /*  "XML"*/ return  "application/xml";
            case ".xul": /*  "XUL"*/ return  "application/vnd.mozilla.xul+xml";
            case ".zip": /*  "ZIP archive"*/ return  "application/zip";
            case ".3gp": /*  "3GPP audio/video container"*/ return  "video/3gpp"; //audio/3gpp if it doesn't contain video
            case ".3g2": /*  "3GPP2 audio/video container"*/ return  "video/3gpp2"; //audio/3gpp2 if it doesn't contain video
            case ".7z": /*  "7-zip archive"*/ return  "application/x-7z-compressed";
        }
        return mimeMap.get(ext);
    }
    
}
/*
https://tika.apache.org/1.14/formats.html#Supported_Document_Formats
*/

/*
https://www.sitepoint.com/mime-types-complete-list/

.3dm x-world/x-3dmf 
.3dmf x-world/x-3dmf 
.a application/octet-stream 
.aab application/x-authorware-bin 
.aam application/x-authorware-map 
.aas application/x-authorware-seg 
.abc text/vnd.abc 
.acgi text/html 
.afl video/animaflex 
.ai application/postscript 
.aif audio/aiff 
.aif audio/x-aiff 
.aifc audio/aiff 
.aifc audio/x-aiff 
.aiff audio/aiff 
.aiff audio/x-aiff 
.aim application/x-aim 
.aip text/x-audiosoft-intra 
.ani application/x-navi-animation 
.aos application/x-nokia-9000-communicator-add-on-software 
.aps application/mime 
.arc application/octet-stream 
.arj application/arj 
.arj application/octet-stream 
.art image/x-jg 
.asf video/x-ms-asf 
.asm text/x-asm 
.asp text/asp 
.asx application/x-mplayer2 
.asx video/x-ms-asf 
.asx video/x-ms-asf-plugin 
.au audio/basic 
.au audio/x-au 
.avi application/x-troff-msvideo 
.avi video/avi 
.avi video/msvideo 
.avi video/x-msvideo 
.avs video/avs-video 
.bcpio application/x-bcpio 
.bin application/mac-binary 
.bin application/macbinary 
.bin application/octet-stream 
.bin application/x-binary 
.bin application/x-macbinary 
.bm image/bmp 
.bmp image/bmp 
.bmp image/x-windows-bmp 
.boo application/book 
.book application/book 
.boz application/x-bzip2 
.bsh application/x-bsh 
.bz application/x-bzip 
.bz2 application/x-bzip2 
.c text/plain 
.c text/x-c 
.c++ text/plain 
.cat application/vnd.ms-pki.seccat 
.cc text/plain 
.cc text/x-c 
.ccad application/clariscad 
.cco application/x-cocoa 
.cdf application/cdf 
.cdf application/x-cdf 
.cdf application/x-netcdf 
.cer application/pkix-cert 
.cer application/x-x509-ca-cert 
.cha application/x-chat 
.chat application/x-chat 
.class application/java 
.class application/java-byte-code 
.class application/x-java-class 
.com application/octet-stream 
.com text/plain 
.conf text/plain 
.cpio application/x-cpio 
.cpp text/x-c 
.cpt application/mac-compactpro 
.cpt application/x-compactpro 
.cpt application/x-cpt 
.crl application/pkcs-crl 
.crl application/pkix-crl 
.crt application/pkix-cert 
.crt application/x-x509-ca-cert 
.crt application/x-x509-user-cert 
.csh application/x-csh 
.csh text/x-script.csh 
.css application/x-pointplus 
.css text/css 
.cxx text/plain 
.dcr application/x-director 
.deepv application/x-deepv 
.def text/plain 
.der application/x-x509-ca-cert 
.dif video/x-dv 
.dir application/x-director 
.dl video/dl 
.dl video/x-dl 
.doc application/msword 
.dot application/msword 
.dp application/commonground 
.drw application/drafting 
.dump application/octet-stream 
.dv video/x-dv 
.dvi application/x-dvi 
.dwf drawing/x-dwf (old) 
.dwf model/vnd.dwf 
.dwg application/acad 
.dwg image/vnd.dwg 
.dwg image/x-dwg 
.dxf application/dxf 
.dxf image/vnd.dwg 
.dxf image/x-dwg 
.dxr application/x-director 
.el text/x-script.elisp 
.elc application/x-bytecode.elisp (compiled elisp) 
.elc application/x-elc 
.env application/x-envoy 
.eps application/postscript 
.es application/x-esrehber 
.etx text/x-setext 
.evy application/envoy 
.evy application/x-envoy 
.exe application/octet-stream 
.f text/plain 
.f text/x-fortran 
.f77 text/x-fortran 
.f90 text/plain 
.f90 text/x-fortran 
.fdf application/vnd.fdf 
.fif application/fractals 
.fif image/fif 
.fli video/fli 
.fli video/x-fli 
.flo image/florian 
.flx text/vnd.fmi.flexstor 
.fmf video/x-atomic3d-feature 
.for text/plain 
.for text/x-fortran 
.fpx image/vnd.fpx 
.fpx image/vnd.net-fpx 
.frl application/freeloader 
.funk audio/make 
.g text/plain 
.g3 image/g3fax 
.gif image/gif 
.gl video/gl 
.gl video/x-gl 
.gsd audio/x-gsm 
.gsm audio/x-gsm 
.gsp application/x-gsp 
.gss application/x-gss 
.gtar application/x-gtar 
.gz application/x-compressed 
.gz application/x-gzip 
.gzip application/x-gzip 
.gzip multipart/x-gzip 
.h text/plain 
.h text/x-h 
.hdf application/x-hdf 
.help application/x-helpfile 
.hgl application/vnd.hp-hpgl 
.hh text/plain 
.hh text/x-h 
.hlb text/x-script 
.hlp application/hlp 
.hlp application/x-helpfile 
.hlp application/x-winhelp 
.hpg application/vnd.hp-hpgl 
.hpgl application/vnd.hp-hpgl 
.hqx application/binhex 
.hqx application/binhex4 
.hqx application/mac-binhex 
.hqx application/mac-binhex40 
.hqx application/x-binhex40 
.hqx application/x-mac-binhex40 
.hta application/hta 
.htc text/x-component 
.htm text/html 
.html text/html 
.htmls text/html 
.htt text/webviewhtml 
.htx text/html 
.ice x-conference/x-cooltalk 
.ico image/x-icon 
.idc text/plain 
.ief image/ief 
.iefs image/ief 
.iges application/iges 
.iges model/iges 
.igs application/iges 
.igs model/iges 
.ima application/x-ima 
.imap application/x-httpd-imap 
.inf application/inf 
.ins application/x-internett-signup 
.ip application/x-ip2 
.isu video/x-isvideo 
.it audio/it 
.iv application/x-inventor 
.ivr i-world/i-vrml 
.ivy application/x-livescreen 
.jam audio/x-jam 
.jav text/plain 
.jav text/x-java-source 
.java text/plain 
.java text/x-java-source 
.jcm application/x-java-commerce 
.jfif image/jpeg 
.jfif image/pjpeg 
.jfif-tbnl image/jpeg 
.jpe image/jpeg 
.jpe image/pjpeg 
.jpeg image/jpeg 
.jpeg image/pjpeg 
.jpg image/jpeg 
.jpg image/pjpeg 
.jps image/x-jps 
.js application/x-javascript 
.js application/javascript 
.js application/ecmascript 
.js text/javascript 
.js text/ecmascript 
.jut image/jutvision 
.kar audio/midi 
.kar music/x-karaoke 
.ksh application/x-ksh 
.ksh text/x-script.ksh 
.la audio/nspaudio 
.la audio/x-nspaudio 
.lam audio/x-liveaudio 
.latex application/x-latex 
.lha application/lha 
.lha application/octet-stream 
.lha application/x-lha 
.lhx application/octet-stream 
.list text/plain 
.lma audio/nspaudio 
.lma audio/x-nspaudio 
.log text/plain 
.lsp application/x-lisp 
.lsp text/x-script.lisp 
.lst text/plain 
.lsx text/x-la-asf 
.ltx application/x-latex 
.lzh application/octet-stream 
.lzh application/x-lzh 
.lzx application/lzx 
.lzx application/octet-stream 
.lzx application/x-lzx 
.m text/plain 
.m text/x-m 
.m1v video/mpeg 
.m2a audio/mpeg 
.m2v video/mpeg 
.m3u audio/x-mpequrl 
.man application/x-troff-man 
.map application/x-navimap 
.mar text/plain 
.mbd application/mbedlet 
.mc$ application/x-magic-cap-package-1.0 
.mcd application/mcad 
.mcd application/x-mathcad 
.mcf image/vasa 
.mcf text/mcf 
.mcp application/netmc 
.me application/x-troff-me 
.mht message/rfc822 
.mhtml message/rfc822 
.mid application/x-midi 
.mid audio/midi 
.mid audio/x-mid 
.mid audio/x-midi 
.mid music/crescendo 
.mid x-music/x-midi 
.midi application/x-midi 
.midi audio/midi 
.midi audio/x-mid 
.midi audio/x-midi 
.midi music/crescendo 
.midi x-music/x-midi 
.mif application/x-frame 
.mif application/x-mif 
.mime message/rfc822 
.mime www/mime 
.mjf audio/x-vnd.audioexplosion.mjuicemediafile 
.mjpg video/x-motion-jpeg 
.mm application/base64 
.mm application/x-meme 
.mme application/base64 
.mod audio/mod 
.mod audio/x-mod 
.moov video/quicktime 
.mov video/quicktime 
.movie video/x-sgi-movie 
.mp2 audio/mpeg 
.mp2 audio/x-mpeg 
.mp2 video/mpeg 
.mp2 video/x-mpeg 
.mp2 video/x-mpeq2a 
.mp3 audio/mpeg3 
.mp3 audio/x-mpeg-3 
.mp3 video/mpeg 
.mp3 video/x-mpeg 
.mpa audio/mpeg 
.mpa video/mpeg 
.mpc application/x-project 
.mpe video/mpeg 
.mpeg video/mpeg 
.mpg audio/mpeg 
.mpg video/mpeg 
.mpga audio/mpeg 
.mpp application/vnd.ms-project 
.mpt application/x-project 
.mpv application/x-project 
.mpx application/x-project 
.mrc application/marc 
.ms application/x-troff-ms 
.mv video/x-sgi-movie 
.my audio/make 
.mzz application/x-vnd.audioexplosion.mzz 
.nap image/naplps 
.naplps image/naplps 
.nc application/x-netcdf 
.ncm application/vnd.nokia.configuration-message 
.nif image/x-niff 
.niff image/x-niff 
.nix application/x-mix-transfer 
.nsc application/x-conference 
.nvd application/x-navidoc 
.o application/octet-stream 
.oda application/oda 
.omc application/x-omc 
.omcd application/x-omcdatamaker 
.omcr application/x-omcregerator 
.p text/x-pascal 
.p10 application/pkcs10 
.p10 application/x-pkcs10 
.p12 application/pkcs-12 
.p12 application/x-pkcs12 
.p7a application/x-pkcs7-signature 
.p7c application/pkcs7-mime 
.p7c application/x-pkcs7-mime 
.p7m application/pkcs7-mime 
.p7m application/x-pkcs7-mime 
.p7r application/x-pkcs7-certreqresp 
.p7s application/pkcs7-signature 
.part application/pro_eng 
.pas text/pascal 
.pbm image/x-portable-bitmap 
.pcl application/vnd.hp-pcl 
.pcl application/x-pcl 
.pct image/x-pict 
.pcx image/x-pcx 
.pdb chemical/x-pdb 
.pdf application/pdf 
.pfunk audio/make 
.pfunk audio/make.my.funk 
.pgm image/x-portable-graymap 
.pgm image/x-portable-greymap 
.pic image/pict 
.pict image/pict 
.pkg application/x-newton-compatible-pkg 
.pko application/vnd.ms-pki.pko 
.pl text/plain 
.pl text/x-script.perl 
.plx application/x-pixclscript 
.pm image/x-xpixmap 
.pm text/x-script.perl-module 
.pm4 application/x-pagemaker 
.pm5 application/x-pagemaker 
.png image/png 
.pnm application/x-portable-anymap 
.pnm image/x-portable-anymap 
.pot application/mspowerpoint 
.pot application/vnd.ms-powerpoint 
.pov model/x-pov 
.ppa application/vnd.ms-powerpoint 
.ppm image/x-portable-pixmap 
.pps application/mspowerpoint 
.pps application/vnd.ms-powerpoint 
.ppt application/mspowerpoint 
.ppt application/powerpoint 
.ppt application/vnd.ms-powerpoint 
.ppt application/x-mspowerpoint 
.ppz application/mspowerpoint 
.pre application/x-freelance 
.prt application/pro_eng 
.ps application/postscript 
.psd application/octet-stream 
.pvu paleovu/x-pv 
.pwz application/vnd.ms-powerpoint 
.py text/x-script.phyton 
.pyc application/x-bytecode.python 
.qcp audio/vnd.qcelp 
.qd3 x-world/x-3dmf 
.qd3d x-world/x-3dmf 
.qif image/x-quicktime 
.qt video/quicktime 
.qtc video/x-qtc 
.qti image/x-quicktime 
.qtif image/x-quicktime 
.ra audio/x-pn-realaudio 
.ra audio/x-pn-realaudio-plugin 
.ra audio/x-realaudio 
.ram audio/x-pn-realaudio 
.ras application/x-cmu-raster 
.ras image/cmu-raster 
.ras image/x-cmu-raster 
.rast image/cmu-raster 
.rexx text/x-script.rexx 
.rf image/vnd.rn-realflash 
.rgb image/x-rgb 
.rm application/vnd.rn-realmedia 
.rm audio/x-pn-realaudio 
.rmi audio/mid 
.rmm audio/x-pn-realaudio 
.rmp audio/x-pn-realaudio 
.rmp audio/x-pn-realaudio-plugin 
.rng application/ringing-tones 
.rng application/vnd.nokia.ringing-tone 
.rnx application/vnd.rn-realplayer 
.roff application/x-troff 
.rp image/vnd.rn-realpix 
.rpm audio/x-pn-realaudio-plugin 
.rt text/richtext 
.rt text/vnd.rn-realtext 
.rtf application/rtf 
.rtf application/x-rtf 
.rtf text/richtext 
.rtx application/rtf 
.rtx text/richtext 
.rv video/vnd.rn-realvideo 
.s text/x-asm 
.s3m audio/s3m 
.saveme application/octet-stream 
.sbk application/x-tbook 
.scm application/x-lotusscreencam 
.scm text/x-script.guile 
.scm text/x-script.scheme 
.scm video/x-scm 
.sdml text/plain 
.sdp application/sdp 
.sdp application/x-sdp 
.sdr application/sounder 
.sea application/sea 
.sea application/x-sea 
.set application/set 
.sgm text/sgml 
.sgm text/x-sgml 
.sgml text/sgml 
.sgml text/x-sgml 
.sh application/x-bsh 
.sh application/x-sh 
.sh application/x-shar 
.sh text/x-script.sh 
.shar application/x-bsh 
.shar application/x-shar 
.shtml text/html 
.shtml text/x-server-parsed-html 
.sid audio/x-psid 
.sit application/x-sit 
.sit application/x-stuffit 
.skd application/x-koan 
.skm application/x-koan 
.skp application/x-koan 
.skt application/x-koan 
.sl application/x-seelogo 
.smi application/smil 
.smil application/smil 
.snd audio/basic 
.snd audio/x-adpcm 
.sol application/solids 
.spc application/x-pkcs7-certificates 
.spc text/x-speech 
.spl application/futuresplash 
.spr application/x-sprite 
.sprite application/x-sprite 
.src application/x-wais-source 
.ssi text/x-server-parsed-html 
.ssm application/streamingmedia 
.sst application/vnd.ms-pki.certstore 
.step application/step 
.stl application/sla 
.stl application/vnd.ms-pki.stl 
.stl application/x-navistyle 
.stp application/step 
.sv4cpio application/x-sv4cpio 
.sv4crc application/x-sv4crc 
.svf image/vnd.dwg 
.svf image/x-dwg 
.svr application/x-world 
.svr x-world/x-svr 
.swf application/x-shockwave-flash 
.t application/x-troff 
.talk text/x-speech 
.tar application/x-tar 
.tbk application/toolbook 
.tbk application/x-tbook 
.tcl application/x-tcl 
.tcl text/x-script.tcl 
.tcsh text/x-script.tcsh 
.tex application/x-tex 
.texi application/x-texinfo 
.texinfo application/x-texinfo 
.text application/plain 
.text text/plain 
.tgz application/gnutar 
.tgz application/x-compressed 
.tif image/tiff 
.tif image/x-tiff 
.tiff image/tiff 
.tiff image/x-tiff 
.tr application/x-troff 
.tsi audio/tsp-audio 
.tsp application/dsptype 
.tsp audio/tsplayer 
.tsv text/tab-separated-values 
.turbot image/florian 
.txt text/plain 
.uil text/x-uil 
.uni text/uri-list 
.unis text/uri-list 
.unv application/i-deas 
.uri text/uri-list 
.uris text/uri-list 
.ustar application/x-ustar 
.ustar multipart/x-ustar 
.uu application/octet-stream 
.uu text/x-uuencode 
.uue text/x-uuencode 
.vcd application/x-cdlink 
.vcs text/x-vcalendar 
.vda application/vda 
.vdo video/vdo 
.vew application/groupwise 
.viv video/vivo 
.viv video/vnd.vivo 
.vivo video/vivo 
.vivo video/vnd.vivo 
.vmd application/vocaltec-media-desc 
.vmf application/vocaltec-media-file 
.voc audio/voc 
.voc audio/x-voc 
.vos video/vosaic 
.vox audio/voxware 
.vqe audio/x-twinvq-plugin 
.vqf audio/x-twinvq 
.vql audio/x-twinvq-plugin 
.vrml application/x-vrml 
.vrml model/vrml 
.vrml x-world/x-vrml 
.vrt x-world/x-vrt 
.vsd application/x-visio 
.vst application/x-visio 
.vsw application/x-visio 
.w60 application/wordperfect6.0 
.w61 application/wordperfect6.1 
.w6w application/msword 
.wav audio/wav 
.wav audio/x-wav 
.wb1 application/x-qpro 
.wbmp image/vnd.wap.wbmp 
.web application/vnd.xara 
.wiz application/msword 
.wk1 application/x-123 
.wmf windows/metafile 
.wml text/vnd.wap.wml 
.wmlc application/vnd.wap.wmlc 
.wmls text/vnd.wap.wmlscript 
.wmlsc application/vnd.wap.wmlscriptc 
.word application/msword 
.wp application/wordperfect 
.wp5 application/wordperfect 
.wp5 application/wordperfect6.0 
.wp6 application/wordperfect 
.wpd application/wordperfect 
.wpd application/x-wpwin 
.wq1 application/x-lotus 
.wri application/mswrite 
.wri application/x-wri 
.wrl application/x-world 
.wrl model/vrml 
.wrl x-world/x-vrml 
.wrz model/vrml 
.wrz x-world/x-vrml 
.wsc text/scriplet 
.wsrc application/x-wais-source 
.wtk application/x-wintalk 
.xbm image/x-xbitmap 
.xbm image/x-xbm 
.xbm image/xbm 
.xdr video/x-amt-demorun 
.xgz xgl/drawing 
.xif image/vnd.xiff 
.xl application/excel 
.xla application/excel 
.xla application/x-excel 
.xla application/x-msexcel 
.xlb application/excel 
.xlb application/vnd.ms-excel 
.xlb application/x-excel 
.xlc application/excel 
.xlc application/vnd.ms-excel 
.xlc application/x-excel 
.xld application/excel 
.xld application/x-excel 
.xlk application/excel 
.xlk application/x-excel 
.xll application/excel 
.xll application/vnd.ms-excel 
.xll application/x-excel 
.xlm application/excel 
.xlm application/vnd.ms-excel 
.xlm application/x-excel 
.xls application/excel 
.xls application/vnd.ms-excel 
.xls application/x-excel 
.xls application/x-msexcel 
.xlt application/excel 
.xlt application/x-excel 
.xlv application/excel 
.xlv application/x-excel 
.xlw application/excel 
.xlw application/vnd.ms-excel 
.xlw application/x-excel 
.xlw application/x-msexcel 
.xm audio/xm 
.xml application/xml 
.xml text/xml 
.xmz xgl/movie 
.xpix application/x-vnd.ls-xpix 
.xpm image/x-xpixmap 
.xpm image/xpm 
.x-png image/png 
.xsr video/x-amt-showrun 
.xwd image/x-xwd 
.xwd image/x-xwindowdump 
.xyz chemical/x-pdb 
.z application/x-compress 
.z application/x-compressed 
.zip application/x-compressed 
.zip application/x-zip-compressed 
.zip application/zip 
.zip multipart/x-zip 
.zoo application/octet-stream 
.zsh text/x-script.zsh 

https://www.freeformatter.com/mime-types-list.html

.aw application/applixware
.atom application/atom+xml
.atomcat application/atomcat+xml
.atomsvc application/atomsvc+xml
.ccxml application/ccxml+xml
.cdmia application/cdmi-capability
.cdmic application/cdmi-container
.cdmid application/cdmi-domain
.cdmio application/cdmi-object
.cdmiq application/cdmi-queue
.cu application/cu-seeme
.davmount application/davmount+xml
.dssc application/dssc+der
.xdssc application/dssc+xml
.es application/ecmascript
.emma application/emma+xml
.epub application/epub+zip
.exi application/exi
.pfr application/font-tdpfr
.stk application/hyperstudio
.ipfix application/ipfix
.jar application/java-archive
.ser application/java-serialized-object
.class application/java-vm
.js application/javascript
.json application/json
.hqx application/mac-binhex40
.cpt application/mac-compactpro
.mads application/mads+xml
.mrc application/marc
.mrcx application/marcxml+xml
.ma application/mathematica
.mathml application/mathml+xml
.mbox application/mbox
.mscml application/mediaservercontrol+xml
.meta4 application/metalink4+xml
.mets application/mets+xml
.mods application/mods+xml
.m21 application/mp21
.mp4 application/mp4
.doc application/msword
.mxf application/mxf
.bin application/octet-stream
.oda application/oda
.opf application/oebps-package+xml
.ogx application/ogg
.onetoc application/onenote
.xer application/patch-ops-error+xml
.pdf application/pdf
.pgp application/pgp-encrypted
.pgp application/pgp-signature
.prf application/pics-rules
.p10 application/pkcs10
.p7m application/pkcs7-mime
.p7s application/pkcs7-signature
.p8 application/pkcs8
.ac application/pkix-attr-cert
.cer application/pkix-cert
.crl application/pkix-crl
.pkipath application/pkix-pkipath
.pki application/pkixcmp
.pls application/pls+xml
.ai application/postscript
.cww application/prs.cww
.pskcxml application/pskc+xml
.rdf application/rdf+xml
.rif application/reginfo+xml
.rnc application/relax-ng-compact-syntax
.rl application/resource-lists+xml
.rld application/resource-lists-diff+xml
.rs application/rls-services+xml
.rsd application/rsd+xml
.rss application/rss+xml
.rtf application/rtf
.sbml application/sbml+xml
.scq application/scvp-cv-request
.scs application/scvp-cv-response
.spq application/scvp-vp-request
.spp application/scvp-vp-response
.sdp application/sdp
.setpay application/set-payment-initiation
.setreg application/set-registration-initiation
.shf application/shf+xml
.smi application/smil+xml
.rq application/sparql-query
.srx application/sparql-results+xml
.gram application/srgs
.grxml application/srgs+xml
.sru application/sru+xml
.ssml application/ssml+xml
.tei application/tei+xml
.tfi application/thraud+xml
.tsd application/timestamped-data
.plb application/vnd.3gpp.pic-bw-large
.psb application/vnd.3gpp.pic-bw-small
.pvb application/vnd.3gpp.pic-bw-var
.tcap application/vnd.3gpp2.tcap
.pwn application/vnd.3m.post-it-notes
.aso application/vnd.accpac.simply.aso
.imp application/vnd.accpac.simply.imp
.acu application/vnd.acucobol
.atc application/vnd.acucorp
.air application/vnd.adobe.air-application-installer-package+zip
.fxp application/vnd.adobe.fxp
.xdp application/vnd.adobe.xdp+xml
.xfdf application/vnd.adobe.xfdf
.ahead application/vnd.ahead.space
.azf application/vnd.airzip.filesecure.azf
.azs application/vnd.airzip.filesecure.azs
.azw application/vnd.amazon.ebook
.acc application/vnd.americandynamics.acc
.ami application/vnd.amiga.ami
.apk application/vnd.android.package-archive
.cii application/vnd.anser-web-certificate-issue-initiation
.fti application/vnd.anser-web-funds-transfer-initiation
.atx application/vnd.antix.game-component
.mpkg application/vnd.apple.installer+xml
.m3u8 application/vnd.apple.mpegurl
.swi application/vnd.aristanetworks.swi
.aep application/vnd.audiograph
.mpm application/vnd.blueice.multipass
.bmi application/vnd.bmi
.rep application/vnd.businessobjects
.cdxml application/vnd.chemdraw+xml
.mmd application/vnd.chipnuts.karaoke-mmd
.cdy application/vnd.cinderella
.cla application/vnd.claymore
.rp9 application/vnd.cloanto.rp9
.c4g application/vnd.clonk.c4group
.c11amc application/vnd.cluetrust.cartomobile-config
.c11amz application/vnd.cluetrust.cartomobile-config-pkg
.csp application/vnd.commonspace
.cdbcmsg application/vnd.contact.cmsg
.cmc application/vnd.cosmocaller
.clkx application/vnd.crick.clicker
.clkk application/vnd.crick.clicker.keyboard
.clkp application/vnd.crick.clicker.palette
.clkt application/vnd.crick.clicker.template
.clkw application/vnd.crick.clicker.wordbank
.wbs application/vnd.criticaltools.wbs+xml
.pml application/vnd.ctc-posml
.ppd application/vnd.cups-ppd
.car application/vnd.curl.car
.pcurl application/vnd.curl.pcurl
.rdz application/vnd.data-vision.rdz
.fe_launch application/vnd.denovo.fcselayout-link
.dna application/vnd.dna
.mlp application/vnd.dolby.mlp
.dpg application/vnd.dpgraph
.dfac application/vnd.dreamfactory
.ait application/vnd.dvb.ait
.svc application/vnd.dvb.service
.geo application/vnd.dynageo
.mag application/vnd.ecowin.chart
.nml application/vnd.enliven
.esf application/vnd.epson.esf
.msf application/vnd.epson.msf
.qam application/vnd.epson.quickanime
.slt application/vnd.epson.salt
.ssf application/vnd.epson.ssf
.es3 application/vnd.eszigno3+xml
.ez2 application/vnd.ezpix-album
.ez3 application/vnd.ezpix-package
.fdf application/vnd.fdf
.seed application/vnd.fdsn.seed
.gph application/vnd.flographit
.ftc application/vnd.fluxtime.clip
.fm application/vnd.framemaker
.fnc application/vnd.frogans.fnc
.ltf application/vnd.frogans.ltf
.fsc application/vnd.fsc.weblaunch
.oas application/vnd.fujitsu.oasys
.oa2 application/vnd.fujitsu.oasys2
.oa3 application/vnd.fujitsu.oasys3
.fg5 application/vnd.fujitsu.oasysgp
.bh2 application/vnd.fujitsu.oasysprs
.ddd application/vnd.fujixerox.ddd
.xdw application/vnd.fujixerox.docuworks
.xbd application/vnd.fujixerox.docuworks.binder
.fzs application/vnd.fuzzysheet
.txd application/vnd.genomatix.tuxedo
.ggb application/vnd.geogebra.file
.ggt application/vnd.geogebra.tool
.gex application/vnd.geometry-explorer
.gxt application/vnd.geonext
.g2w application/vnd.geoplan
.g3w application/vnd.geospace
.gmx application/vnd.gmx
.kml application/vnd.google-earth.kml+xml
.kmz application/vnd.google-earth.kmz
.gqf application/vnd.grafeq
.gac application/vnd.groove-account
.ghf application/vnd.groove-help
.gim application/vnd.groove-identity-message
.grv application/vnd.groove-injector
.gtm application/vnd.groove-tool-message
.tpl application/vnd.groove-tool-template
.vcg application/vnd.groove-vcard
.hal application/vnd.hal+xml
.zmm application/vnd.handheld-entertainment+xml
.hbci application/vnd.hbci
.les application/vnd.hhe.lesson-player
.hpgl application/vnd.hp-hpgl
.hpid application/vnd.hp-hpid
.hps application/vnd.hp-hps
.jlt application/vnd.hp-jlyt
.pcl application/vnd.hp-pcl
.pclxl application/vnd.hp-pclxl
.sfd-hdstx application/vnd.hydrostatix.sof-data
.x3d application/vnd.hzn-3d-crossword
.mpy application/vnd.ibm.minipay
.afp application/vnd.ibm.modcap
.irm application/vnd.ibm.rights-management
.sc application/vnd.ibm.secure-container
.icc application/vnd.iccprofile
.igl application/vnd.igloader
.ivp application/vnd.immervision-ivp
.ivu application/vnd.immervision-ivu
.igm application/vnd.insors.igm
.xpw application/vnd.intercon.formnet
.i2g application/vnd.intergeo
.qbo application/vnd.intu.qbo
.qfx application/vnd.intu.qfx
.rcprofile application/vnd.ipunplugged.rcprofile
.irp application/vnd.irepository.package+xml
.xpr application/vnd.is-xpr
.fcs application/vnd.isac.fcs
.jam application/vnd.jam
.rms application/vnd.jcp.javame.midlet-rms
.jisp application/vnd.jisp
.joda application/vnd.joost.joda-archive
.ktz application/vnd.kahootz
.karbon application/vnd.kde.karbon
.chrt application/vnd.kde.kchart
.kfo application/vnd.kde.kformula
.flw application/vnd.kde.kivio
.kon application/vnd.kde.kontour
.kpr application/vnd.kde.kpresenter
.ksp application/vnd.kde.kspread
.kwd application/vnd.kde.kword
.htke application/vnd.kenameaapp
.kia application/vnd.kidspiration
.kne application/vnd.kinar
.skp application/vnd.koan
.sse application/vnd.kodak-descriptor
.lasxml application/vnd.las.las+xml
.lbd application/vnd.llamagraphics.life-balance.desktop
.lbe application/vnd.llamagraphics.life-balance.exchange+xml
.123 application/vnd.lotus-1-2-3
.apr application/vnd.lotus-approach
.pre application/vnd.lotus-freelance
.nsf application/vnd.lotus-notes
.org application/vnd.lotus-organizer
.scm application/vnd.lotus-screencam
.lwp application/vnd.lotus-wordpro
.portpkg application/vnd.macports.portpkg
.mcd application/vnd.mcd
.mc1 application/vnd.medcalcdata
.cdkey application/vnd.mediastation.cdkey
.mwf application/vnd.mfer
.mfm application/vnd.mfmp
.flo application/vnd.micrografx.flo
.igx application/vnd.micrografx.igx
.mif application/vnd.mif
.daf application/vnd.mobius.daf
.dis application/vnd.mobius.dis
.mbk application/vnd.mobius.mbk
.mqy application/vnd.mobius.mqy
.msl application/vnd.mobius.msl
.plc application/vnd.mobius.plc
.txf application/vnd.mobius.txf
.mpn application/vnd.mophun.application
.mpc application/vnd.mophun.certificate
.xul application/vnd.mozilla.xul+xml
.cil application/vnd.ms-artgalry
.cab application/vnd.ms-cab-compressed
.xls application/vnd.ms-excel
.xlam application/vnd.ms-excel.addin.macroenabled.12
.xlsb application/vnd.ms-excel.sheet.binary.macroenabled.12
.xlsm application/vnd.ms-excel.sheet.macroenabled.12
.xltm application/vnd.ms-excel.template.macroenabled.12
.eot application/vnd.ms-fontobject
.chm application/vnd.ms-htmlhelp
.ims application/vnd.ms-ims
.lrm application/vnd.ms-lrm
.thmx application/vnd.ms-officetheme
.cat application/vnd.ms-pki.seccat
.stl application/vnd.ms-pki.stl
.ppt application/vnd.ms-powerpoint
.ppam application/vnd.ms-powerpoint.addin.macroenabled.12
.pptm application/vnd.ms-powerpoint.presentation.macroenabled.12
.sldm application/vnd.ms-powerpoint.slide.macroenabled.12
.ppsm application/vnd.ms-powerpoint.slideshow.macroenabled.12
.potm application/vnd.ms-powerpoint.template.macroenabled.12
.mpp application/vnd.ms-project
.docm application/vnd.ms-word.document.macroenabled.12
.dotm application/vnd.ms-word.template.macroenabled.12
.wps application/vnd.ms-works
.wpl application/vnd.ms-wpl
.xps application/vnd.ms-xpsdocument
.mseq application/vnd.mseq
.mus application/vnd.musician
.msty application/vnd.muvee.style
.nlu application/vnd.neurolanguage.nlu
.nnd application/vnd.noblenet-directory
.nns application/vnd.noblenet-sealer
.nnw application/vnd.noblenet-web
.ngdat application/vnd.nokia.n-gage.data
.n-gage application/vnd.nokia.n-gage.symbian.install
.rpst application/vnd.nokia.radio-preset
.rpss application/vnd.nokia.radio-presets
.edm application/vnd.novadigm.edm
.edx application/vnd.novadigm.edx
.ext application/vnd.novadigm.ext
.odc application/vnd.oasis.opendocument.chart
.otc application/vnd.oasis.opendocument.chart-template
.odb application/vnd.oasis.opendocument.database
.odf application/vnd.oasis.opendocument.formula
.odft application/vnd.oasis.opendocument.formula-template
.odg application/vnd.oasis.opendocument.graphics
.otg application/vnd.oasis.opendocument.graphics-template
.odi application/vnd.oasis.opendocument.image
.oti application/vnd.oasis.opendocument.image-template
.odp application/vnd.oasis.opendocument.presentation
.otp application/vnd.oasis.opendocument.presentation-template
.ods application/vnd.oasis.opendocument.spreadsheet
.ots application/vnd.oasis.opendocument.spreadsheet-template
.odt application/vnd.oasis.opendocument.text
.odm application/vnd.oasis.opendocument.text-master
.ott application/vnd.oasis.opendocument.text-template
.oth application/vnd.oasis.opendocument.text-web
.xo application/vnd.olpc-sugar
.dd2 application/vnd.oma.dd2+xml
.oxt application/vnd.openofficeorg.extension
.pptx application/vnd.openxmlformats-officedocument.presentationml.presentation
.sldx application/vnd.openxmlformats-officedocument.presentationml.slide
.ppsx application/vnd.openxmlformats-officedocument.presentationml.slideshow
.potx application/vnd.openxmlformats-officedocument.presentationml.template
.xlsx application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
.xltx application/vnd.openxmlformats-officedocument.spreadsheetml.template
.docx application/vnd.openxmlformats-officedocument.wordprocessingml.document
.dotx application/vnd.openxmlformats-officedocument.wordprocessingml.template
.mgp application/vnd.osgeo.mapguide.package
.dp application/vnd.osgi.dp
.pdb application/vnd.palm
.paw application/vnd.pawaafile
.str application/vnd.pg.format
.ei6 application/vnd.pg.osasli
.efif application/vnd.picsel
.wg application/vnd.pmi.widget
.plf application/vnd.pocketlearn
.pbd application/vnd.powerbuilder6
.box application/vnd.previewsystems.box
.mgz application/vnd.proteus.magazine
.qps application/vnd.publishare-delta-tree
.ptid application/vnd.pvi.ptid1
.qxd application/vnd.quark.quarkxpress
.bed application/vnd.realvnc.bed
.mxl application/vnd.recordare.musicxml
.musicxml application/vnd.recordare.musicxml+xml
.cryptonote application/vnd.rig.cryptonote
.cod application/vnd.rim.cod
.rm application/vnd.rn-realmedia
.link66 application/vnd.route66.link66+xml
.st application/vnd.sailingtracker.track
.see application/vnd.seemail
.sema application/vnd.sema
.semd application/vnd.semd
.semf application/vnd.semf
.ifm application/vnd.shana.informed.formdata
.itp application/vnd.shana.informed.formtemplate
.iif application/vnd.shana.informed.interchange
.ipk application/vnd.shana.informed.package
.twd application/vnd.simtech-mindmapper
.mmf application/vnd.smaf
.teacher application/vnd.smart.teacher
.sdkm application/vnd.solent.sdkm+xml
.dxp application/vnd.spotfire.dxp
.sfs application/vnd.spotfire.sfs
.sdc application/vnd.stardivision.calc
.sda application/vnd.stardivision.draw
.sdd application/vnd.stardivision.impress
.smf application/vnd.stardivision.math
.sdw application/vnd.stardivision.writer
.sgl application/vnd.stardivision.writer-global
.sm application/vnd.stepmania.stepchart
.sxc application/vnd.sun.xml.calc
.stc application/vnd.sun.xml.calc.template
.sxd application/vnd.sun.xml.draw
.std application/vnd.sun.xml.draw.template
.sxi application/vnd.sun.xml.impress
.sti application/vnd.sun.xml.impress.template
.sxm application/vnd.sun.xml.math
.sxw application/vnd.sun.xml.writer
.sxg application/vnd.sun.xml.writer.global
.stw application/vnd.sun.xml.writer.template
.sus application/vnd.sus-calendar
.svd application/vnd.svd
.sis application/vnd.symbian.install
.xsm application/vnd.syncml+xml
.bdm application/vnd.syncml.dm+wbxml
.xdm application/vnd.syncml.dm+xml
.tao application/vnd.tao.intent-module-archive
.tmo application/vnd.tmobile-livetv
.tpt application/vnd.trid.tpt
.mxs application/vnd.triscape.mxs
.tra application/vnd.trueapp
.ufd application/vnd.ufdl
.utz application/vnd.uiq.theme
.umj application/vnd.umajin
.unityweb application/vnd.unity
.uoml application/vnd.uoml+xml
.vcx application/vnd.vcx
.vsd application/vnd.visio
.vsdx application/vnd.visio2013
.vis application/vnd.visionary
.vsf application/vnd.vsf
.wbxml application/vnd.wap.wbxml
.wmlc application/vnd.wap.wmlc
.wmlsc application/vnd.wap.wmlscriptc
.wtb application/vnd.webturbo
.nbp application/vnd.wolfram.player
.wpd application/vnd.wordperfect
.wqd application/vnd.wqd
.stf application/vnd.wt.stf
.xar application/vnd.xara
.xfdl application/vnd.xfdl
.hvd application/vnd.yamaha.hv-dic
.hvs application/vnd.yamaha.hv-script
.hvp application/vnd.yamaha.hv-voice
.osf application/vnd.yamaha.openscoreformat
.osfpvg application/vnd.yamaha.openscoreformat.osfpvg+xml
.saf application/vnd.yamaha.smaf-audio
.spf application/vnd.yamaha.smaf-phrase
.cmp application/vnd.yellowriver-custom-menu
.zir application/vnd.zul
.zaz application/vnd.zzazz.deck+xml
.vxml application/voicexml+xml
.wgt application/widget
.hlp application/winhlp
.wsdl application/wsdl+xml
.wspolicy application/wspolicy+xml
.7z application/x-7z-compressed
.abw application/x-abiword
.ace application/x-ace-compressed
.aab application/x-authorware-bin
.aam application/x-authorware-map
.aas application/x-authorware-seg
.bcpio application/x-bcpio
.torrent application/x-bittorrent
.bz application/x-bzip
.bz2 application/x-bzip2
.vcd application/x-cdlink
.chat application/x-chat
.pgn application/x-chess-pgn
.cpio application/x-cpio
.csh application/x-csh
.deb application/x-debian-package
.dir application/x-director
.wad application/x-doom
.ncx application/x-dtbncx+xml
.dtb application/x-dtbook+xml
.res application/x-dtbresource+xml
.dvi application/x-dvi
.bdf application/x-font-bdf
.gsf application/x-font-ghostscript
.psf application/x-font-linux-psf
.otf application/x-font-otf
.pcf application/x-font-pcf
.snf application/x-font-snf
.ttf application/x-font-ttf
.pfa application/x-font-type1
.woff application/x-font-woff
.spl application/x-futuresplash
.gnumeric application/x-gnumeric
.gtar application/x-gtar
.hdf application/x-hdf
.jnlp application/x-java-jnlp-file
.latex application/x-latex
.prc application/x-mobipocket-ebook
.application application/x-ms-application
.wmd application/x-ms-wmd
.wmz application/x-ms-wmz
.xbap application/x-ms-xbap
.mdb application/x-msaccess
.obd application/x-msbinder
.crd application/x-mscardfile
.clp application/x-msclip
.exe application/x-msdownload
.mvb application/x-msmediaview
.wmf application/x-msmetafile
.mny application/x-msmoney
.pub application/x-mspublisher
.scd application/x-msschedule
.trm application/x-msterminal
.wri application/x-mswrite
.nc application/x-netcdf
.p12 application/x-pkcs12
.p7b application/x-pkcs7-certificates
.p7r application/x-pkcs7-certreqresp
.rar application/x-rar-compressed
.sh application/x-sh
.shar application/x-shar
.swf application/x-shockwave-flash
.xap application/x-silverlight-app
.sit application/x-stuffit
.sitx application/x-stuffitx
.sv4cpio application/x-sv4cpio
.sv4crc application/x-sv4crc
.tar application/x-tar
.tcl application/x-tcl
.tex application/x-tex
.tfm application/x-tex-tfm
.texinfo application/x-texinfo
.ustar application/x-ustar
.src application/x-wais-source
.der application/x-x509-ca-cert
.fig application/x-xfig
.xpi application/x-xpinstall
.xdf application/xcap-diff+xml
.xenc application/xenc+xml
.xhtml application/xhtml+xml
.xml application/xml
.dtd application/xml-dtd
.xop application/xop+xml
.xslt application/xslt+xml
.xspf application/xspf+xml
.mxml application/xv+xml
.yang application/yang
.yin application/yin+xml
.zip application/zip
.adp audio/adpcm
.au audio/basic
.mid audio/midi
.mp4a audio/mp4
.mpga audio/mpeg
.oga audio/ogg
.uva audio/vnd.dece.audio
.eol audio/vnd.digital-winds
.dra audio/vnd.dra
.dts audio/vnd.dts
.dtshd audio/vnd.dts.hd
.lvp audio/vnd.lucent.voice
.pya audio/vnd.ms-playready.media.pya
.ecelp4800 audio/vnd.nuera.ecelp4800
.ecelp7470 audio/vnd.nuera.ecelp7470
.ecelp9600 audio/vnd.nuera.ecelp9600
.rip audio/vnd.rip
.weba audio/webm
.aac audio/x-aac
.aif audio/x-aiff
.m3u audio/x-mpegurl
.wax audio/x-ms-wax
.wma audio/x-ms-wma
.ram audio/x-pn-realaudio
.rmp audio/x-pn-realaudio-plugin
.wav audio/x-wav
.cdx chemical/x-cdx
.cif chemical/x-cif
.cmdf chemical/x-cmdf
.cml chemical/x-cml
.csml chemical/x-csml
.xyz chemical/x-xyz
.bmp image/bmp
.cgm image/cgm
.g3 image/g3fax
.gif image/gif
.ief image/ief
.jpeg image/jpeg
.jpg image/jpeg
.pjpeg image/pjpeg
.ktx image/ktx
.png image/png
.png image/x-png
.png image/x-citrix-png
.btif image/prs.btif
.svg image/svg+xml
.tiff image/tiff
.psd image/vnd.adobe.photoshop
.uvi image/vnd.dece.graphic
.sub image/vnd.dvb.subtitle
.djvu image/vnd.djvu
.dwg image/vnd.dwg
.dxf image/vnd.dxf
.fbs image/vnd.fastbidsheet
.fpx image/vnd.fpx
.fst image/vnd.fst
.mmr image/vnd.fujixerox.edmics-mmr
.rlc image/vnd.fujixerox.edmics-rlc
.mdi image/vnd.ms-modi
.npx image/vnd.net-fpx
.wbmp image/vnd.wap.wbmp
.xif image/vnd.xiff
.webp image/webp
.ras image/x-cmu-raster
.cmx image/x-cmx
.fh image/x-freehand
.ico image/x-icon
.pcx image/x-pcx
.pic image/x-pict
.pnm image/x-portable-anymap
.pbm image/x-portable-bitmap
.pgm image/x-portable-graymap
.ppm image/x-portable-pixmap
.rgb image/x-rgb
.xbm image/x-xbitmap
.xpm image/x-xpixmap
.xwd image/x-xwindowdump
.eml message/rfc822
.igs model/iges
.msh model/mesh
.dae model/vnd.collada+xml
.dwf model/vnd.dwf
.gdl model/vnd.gdl
.gtw model/vnd.gtw
.mts model/vnd.mts
.vtu model/vnd.vtu
.wrl model/vrml
.ics text/calendar
.css text/css
.csv text/csv
.html text/html
.n3 text/n3
.txt text/plain
.dsc text/prs.lines.tag
.rtx text/richtext
.sgml text/sgml
.tsv text/tab-separated-values
.t text/troff
.ttl text/turtle
.uri text/uri-list
.curl text/vnd.curl
.dcurl text/vnd.curl.dcurl
.scurl text/vnd.curl.scurl
.mcurl text/vnd.curl.mcurl
.fly text/vnd.fly
.flx text/vnd.fmi.flexstor
.gv text/vnd.graphviz
.3dml text/vnd.in3d.3dml
.spot text/vnd.in3d.spot
.jad text/vnd.sun.j2me.app-descriptor
.wml text/vnd.wap.wml
.wmls text/vnd.wap.wmlscript
.s text/x-asm
.c text/x-c
.f text/x-fortran
.p text/x-pascal
.java text/x-java-source
.etx text/x-setext
.uu text/x-uuencode
.vcs text/x-vcalendar
.vcf text/x-vcard
.3gp video/3gpp
.3g2 video/3gpp2
.h261 video/h261
.h263 video/h263
.h264 video/h264
.jpgv video/jpeg
.jpm video/jpm
.mj2 video/mj2
.mp4 video/mp4
.mpeg video/mpeg
.ogv video/ogg
.qt video/quicktime
.uvh video/vnd.dece.hd
.uvm video/vnd.dece.mobile
.uvp video/vnd.dece.pd
.uvs video/vnd.dece.sd
.uvv video/vnd.dece.video
.fvt video/vnd.fvt
.mxu video/vnd.mpegurl
.pyv video/vnd.ms-playready.media.pyv
.uvu video/vnd.uvvu.mp4
.viv video/vnd.vivo
.webm video/webm
.f4v video/x-f4v
.fli video/x-fli
.flv video/x-flv
.m4v video/x-m4v
.asf video/x-ms-asf
.wm video/x-ms-wm
.wmv video/x-ms-wmv
.wmx video/x-ms-wmx
.wvx video/x-ms-wvx
.avi video/x-msvideo
.movie video/x-sgi-movie
.ice x-conference/x-cooltalk
.par text/plain-bas
.yaml text/yaml
.dmg application/x-apple-diskimage
*/