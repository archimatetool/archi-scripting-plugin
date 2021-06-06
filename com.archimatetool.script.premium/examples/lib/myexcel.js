$JExcel = {
};


// Pending runText formatting http://officeopenxml.com/SSstyles.php
(function () {

    var borderKind = ["left", "right", "top", "bottom"];                                    // Not implementing diagonal borders, as they require an additonal attributes: diagonalUp diagonalDown
    var horAlign = ["LEFT", "CENTER", "RIGHT", "NONE"];
    var vertAlign = ["TOP", "CENTER", "BOTTOM", "NONE"];
    var align = {
        L: "left", C: "center", R: "right", T: "top", B: "bottom"
    }

    function componentToHex(c) {
        var hex = c.toString(16);
        return hex.length == 1 ? "0" + hex : hex;
    }

    $JExcel.rgbToHex = function (r, g, b) {
        if (r == undefined || g == undefined || b == undefined) return undefined;
        return (componentToHex(r) + componentToHex(g) + componentToHex(b)).toUpperCase();
    }

	$JExcel.toExcelUTCTime = function (date1){
		var d2=Math.floor(date1.getTime()/1000);													// Number of seconds since JS epoch
		d2=Math.floor(d2/86400)+25569;																// Days since epoch plus difference in days between Excel EPOCH and JS epoch
		
		var seconds = date1.getUTCSeconds()+60*date1.getUTCMinutes()+60*60*date1.getUTCHours();		// Number of seconds of received hour
		var SECS_DAY= 60 * 60 * 24;																	// Number of seconds of a day
		return d2+(seconds/SECS_DAY);																// Returns a local time !!
	}

	$JExcel.toExcelLocalTime = function (date1){
		var d2=Math.floor(date1.getTime()/1000);													// Number of seconds since JS epoch
		d2=Math.floor(d2/86400)+25569;																// Days since epoch plus difference in days between Excel EPOCH and JS epoch
		var seconds = date1.getUTCSeconds()+60*date1.getUTCMinutes()+60*60*date1.getUTCHours();		// Number of seconds of received hour
		seconds = seconds-60*(date1.getTimezoneOffset());											// Differences in seconds between UTC and LOCAL this depends on date becase daylight saving time
		var SECS_DAY= 60 * 60 * 24;																	// Number of seconds of a day
		return d2+(seconds/SECS_DAY);																// Returns a local time !!
	}

    // For styles see page 2127-2143 of the standard at
    // http://www.ecma-international.org/news/TC45_current_work/Office%20Open%20XML%20Part%204%20-%20Markup%20Language%20Reference.pdf

    var BuiltInFormats = [];
    BuiltInFormats[0] = 'General';
    BuiltInFormats[1] = '0';
    BuiltInFormats[2] = '0.00';
    BuiltInFormats[3] = '#,##0';
    BuiltInFormats[4] = '#,##0.00';

    BuiltInFormats[9] = '0%';
    BuiltInFormats[10] = '0.00%';
    BuiltInFormats[11] = '0.00E+00';
    BuiltInFormats[12] = '# ?/?';
    BuiltInFormats[13] = '# ??/??';
    BuiltInFormats[14] = 'mm-dd-yy';
    BuiltInFormats[15] = 'd-mmm-yy';
    BuiltInFormats[16] = 'd-mmm';
    BuiltInFormats[17] = 'mmm-yy';
    BuiltInFormats[18] = 'h:mm AM/PM';
    BuiltInFormats[19] = 'h:mm:ss AM/PM';
    BuiltInFormats[20] = 'h:mm';
    BuiltInFormats[21] = 'h:mm:ss';
    BuiltInFormats[22] = 'm/d/yy h:mm';

    BuiltInFormats[27] = '[$-404]e/m/d';
    BuiltInFormats[30] = 'm/d/yy';
    BuiltInFormats[36] = '[$-404]e/m/d';

    BuiltInFormats[37] = '#,##0 ;(#,##0)';
    BuiltInFormats[38] = '#,##0 ;[Red](#,##0)';
    BuiltInFormats[39] = '#,##0.00;(#,##0.00)';
    BuiltInFormats[40] = '#,##0.00;[Red](#,##0.00)';

    BuiltInFormats[44] = '_("$"* #,##0.00_);_("$"* \(#,##0.00\);_("$"* "-"??_);_(@_)';
    BuiltInFormats[45] = 'mm:ss';
    BuiltInFormats[46] = '[h]:mm:ss';
    BuiltInFormats[47] = 'mmss.0';
    BuiltInFormats[48] = '##0.0E+0';
    BuiltInFormats[49] = '@';

    BuiltInFormats[50] = '[$-404]e/m/d';
    BuiltInFormats[57] = '[$-404]e/m/d';
    BuiltInFormats[59] = 't0';
    BuiltInFormats[60] = 't0.00';
    BuiltInFormats[61] = 't#,##0';
    BuiltInFormats[62] = 't#,##0.00';
    BuiltInFormats[67] = 't0%';
    BuiltInFormats[68] = 't0.00%';
    BuiltInFormats[69] = 't# ?/?';
    BuiltInFormats[70] = 't# ??/??';
    BuiltInFormats[165] = '*********';              // Here we start with non hardcoded formats
    var baseFormats = 166;                              // Formats below this one are builtInt

    $JExcel.formats = BuiltInFormats;

    $JExcel.borderStyles = [
        "none", "thin", "medium", "dashed", "dotted", "thick", "double", "hair", "mediumDashed",
        "dashDot", "mediumDashDot", "dashDotDot", "mediumDashDotDot", "slantDashDot"];

    var borderStylesUpper = [];
    for (var i = 0; i < $JExcel.borderStyles.length; i++) borderStylesUpper.push($JExcel.borderStyles[i].toUpperCase());




    var templateSheet = '<?xml version="1.0" ?><worksheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" ' +
                'xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006" xmlns:mv="urn:schemas-microsoft-com:mac:vml" ' +
                'xmlns:mx="http://schemas.microsoft.com/office/mac/excel/2008/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships" ' +
                'xmlns:x14="http://schemas.microsoft.com/office/spreadsheetml/2009/9/main" xmlns:x14ac="http://schemas.microsoft.com/office/spreadsheetml/2009/9/ac" ' +
                'xmlns:xm="http://schemas.microsoft.com/office/excel/2006/main">' +
                '{columns}' +
                '<sheetData>{rows}</sheetData></worksheet>';


    // --------------------- BEGIN of generic UTILS
    function getArray(v) {
        if (!v) return undefined;
        return (v.constructor === Array) ? v.slice() : undefined;
    }

    function findOrAdd(list, value) {
        var i = list.indexOf(value);
        if (i != -1) return i;
        list.push(value);
        return list.length - 1;
    }

    function pushV(list, value) {
        list.push(value);
        return value;
    }

    function pushI(list, value) {
        list.push(value);
        return list.length - 1;
    }

    function setV(list, index, value) {
        list[index] = value;
        return value;
    }

    // --------------------- END of generic UTILS



    // --------------------- BEGIN Handling of sheets 
    function toWorkBookSheet(sheet) {
        return '<sheet state="visible" name="' + sheet.name + '" sheetId="' + sheet.id + '" r:id="' + sheet.rId + '"/>';
    }

    function toWorkBookRel(sheet, i) {
        return '<Relationship Id="' + sheet.rId + '" Target="worksheets/sheet' + i + '.xml" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/worksheet"/>';
    }


    function getAsXml(sheet) {
        return templateSheet.replace('{columns}', generateColums(sheet.columns)).replace("{rows}", generateRows(sheet.rows));
    }


    // ------------------- BEGIN Sheet DATA Handling
    function setSheet(value, style, size) {
        this.name = value;                                                      // The only think that we can set in a sheet Is the name
    }

    function getRow(y) {
        return (this.rows[y] ? this.rows[y] : setV(this.rows, y, { cells: [] }));                                                        // If there is a row return it, otherwise create it and return it
    }

    function getColumn(x) {
        return (this.columns[x] ? this.columns[x] : setV(this.columns, x, {}));                                                          // If there is a column return it, otherwise create it and return it
    }

    function getCell(x, y) {
        var row = this.getRow(y).cells;                                                                                                  // Get the row a,d its DATA component
        return (row[x] ? row[x] : setV(row, x, {}));
    }

    function setCell(cell, value, style) {
        if (value != undefined) cell.v = value;
        if (style) cell.s = style;
    }

    function setColumn(column, value, style) {
        if (value != undefined) column.wt = value;
        if (style) column.style = style;
    }

    function setRow(row, value, style) {
        if (value && !isNaN(value)) row.ht = value;
        if (style) row.style = style;
    }

    // ------------------- END Sheet DATA Handling


    function createSheets() {
        var oSheets = {
            sheets: [],
            add: function (name) {
                var sheet = { id: this.sheets.length + 1, rId: "rId" + (3 + this.sheets.length), name: name, rows: [], columns: [], getColumn: getColumn, set: setSheet, getRow: getRow, getCell: getCell };
                return pushI(this.sheets, sheet);
            },
            get: function (index) {
                var sheet = this.sheets[index];
                if (!sheet) throw "Bad sheet " + index;
                return sheet;
            },



            rows: function (i) {
                if (i < 0 || i >= this.sheets.length) throw "Bad sheet number must be [0.." + (this.sheets.length - 1) + "] and is: " + i;
                return this.sheets[i].rows;
            },
            setWidth: function (sheet, column, value, style) {
                // See 3.3.1.12 col (Column Width & Formatting
                if (value) this.sheets[sheet].colWidths[column] = isNaN(value) ? value.toString().toLowerCase() : value;
                if (style) this.sheets[sheet].colStyles[column] = style;
            },

            toWorkBook: function () {
                var s = '<?xml version="1.0" standalone="yes"?>' +
                    '<workbook xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" xmlns:r="http://schemas.openxmlformats.org/officeDocument/2006/relationships">' +
                    '<sheets>';
                for (var i = 0; i < this.sheets.length; i++) s = s + toWorkBookSheet(this.sheets[i]);
                return s + '</sheets><calcPr/></workbook>';
            },
            toWorkBookRels: function () {
                var s = '<?xml version="1.0" ?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">';
                s = s + '<Relationship Id="rId2" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/styles" Target="styles.xml"/>';                      // rId2 is hardcoded and reserved for STYLES
                for (var i = 0; i < this.sheets.length; i++) s = s + toWorkBookRel(this.sheets[i], i + 1);
                return s + '</Relationships>';
            },
            toRels: function () {
                var s = '<?xml version="1.0" encoding="UTF-8" standalone="yes"?><Relationships xmlns="http://schemas.openxmlformats.org/package/2006/relationships">';
                s = s + '<Relationship Id="rId1" Type="http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument" Target="xl/workbook.xml"/>';         // rId1 is reserverd for WorkBook
                return s + '</Relationships>';
            },
            toContentType: function () {
                var s = '<?xml version="1.0" standalone="yes" ?><Types xmlns="http://schemas.openxmlformats.org/package/2006/content-types"><Default ContentType="application/xml" Extension="xml"/>';
                s = s + '<Default ContentType="application/vnd.openxmlformats-package.relationships+xml" Extension="rels"/>';
                s = s + '<Override ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet.main+xml" PartName="/xl/workbook.xml"/>';
                s = s + '<Override ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.styles+xml" PartName="/xl/styles.xml" />';
                for (var i = 1; i <= this.sheets.length; i++) s = s + '<Override ContentType="application/vnd.openxmlformats-officedocument.spreadsheetml.worksheet+xml" PartName="/xl/worksheets/sheet' + i + '.xml"/>';
                return s + '</Types>';
            },
            fileData: function (xl) {
                for (var i = 0; i < this.sheets.length; i++) {
                    xl.file('worksheets/sheet' + (i + 1) + '.xml', getAsXml(this.sheets[i]));
                }
            }
        };
        return oSheets;
    }
    // --------------------- END Handling of sheets 

    // --------------------- BEGIN Handling of style

    function toFontXml(f) {
        var f = f.split(";");
        return '<font>' +
            (f[3].indexOf("B") > -1 ? '<b />' : '') +
            (f[3].indexOf("I") > -1 ? '<i />' : '') +
            (f[3].indexOf("U") > -1 ? '<u />' : '') +
            (f[1] != "_" ? '<sz val="' + f[1] + '" />' : '') +
            (f[2] != "_" ? '<color rgb="FF' + f[2] + '" />' : '') +
            (f[0] ? '<name val="' + f[0] + '" />' : '') +
            '</font>';   // <family val="2" /><scheme val="minor" />

    }

    function toFillXml(f) {
        return '<fill><patternFill patternType="solid"><fgColor rgb="FF' + f + '" /><bgColor indexed="64" /></patternFill ></fill>';
    }

    function toBorderXml(b) {
        var s = "<border>";
        b = b.split(",");
        for (var i = 0; i < 4; i++) {
            var vals = b[i].split(" ");
            s = s + "<" + borderKind[i];
            if (vals[0] == "NONE") s = s + "/>";
            else {
                var border = $JExcel.borderStyles[borderStylesUpper.indexOf(vals[0])];
                if (border)
                    s = s + ' style="' + border + '" >' + (vals[1] != "NONE" ? '<color rgb="FF' + vals[1].substring(1) + '"/>' : '');
                else
                    s = s + ">";
                s = s + "</" + borderKind[i] + ">";
            }
        }
        return s + "<diagonal/></border>";
    }

    function replaceAll(where, search, replacement) {
        return where.split(search).join(replacement);
    };

    function replaceAllMultiple(where, search, replacement) {
        while (where.indexOf(search) != -1) where = replaceAll(where, search, replacement);
        return where;
    }


    function toStyleXml(style) {
        var alignXml = "";
        if (style.align) {
            var h = align[style.align.charAt(0)];
            var v = align[style.align.charAt(1)];
            if (h || v) {
                alignXml = "<alignment ";
                if (h) alignXml = alignXml + ' horizontal="' + h + '" ';
                if (v) alignXml = alignXml + ' vertical="' + v + '" ';
                alignXml = alignXml + " />";
            }
        }
        var s = '<xf numFmtId="' + style.format + '" fontId="' + style.font + '" fillId="' + style.fill + '" borderId="' + style.border + '" xfId="0" ';
        if (style.border != 0) s = s + ' applyBorder="1" ';
        if (style.format >= baseFormats) s = s + ' applyNumberFormat="1" ';
        if (style.fill != 0) s = s + ' applyFill="1" ';
        if (alignXml != "") s = s + ' applyAlignment="1" ';
        s = s + '>';
        s = s + alignXml;
        return s + "</xf>";
    }

    //"Arial", 14, "#0000EE","UBI"

    function normalizeFont(fontDescription) {
        fontDescription = replaceAllMultiple(fontDescription, "  ", " ");
        var fNormalized = ["_", "_", "_", "_"];                                 //  Name - Size - Color - Style (use NONE as placeholder) 
        var i = 0, list = fontDescription.split(" ");                       //  Split by " "
        var name = [];
        while (list[0] && (list[0] != "none") && (isNaN(list[0])) && (list[0].charAt(0) != "#")) {
            name.push(list[0].charAt(0).toUpperCase() + list[0].substring(1).toLowerCase());
            list.splice(0, 1);
        }

        fNormalized[0] = name.join(" ");
        while (list[0] == "none") list.splice(0, 1);                        // Delete any "none" that we might have
        if (!isNaN(list[0])) {                                              // IF we have a number then this is the font size    
            fNormalized[1] = list[0];
            list.splice(0, 1);
        }
        while (list[0] == "none") list.splice(0, 1);                        // Delete any "none" that we might have
        if (list[0] && list[0].length == 7 && list[0].charAt(0) == "#") {      // IF we have a 6 digits value it must be the color
            fNormalized[2] = list[0].substring(1).toUpperCase();
            list.splice(0, 1);
        }
        while (list[0] == "none") list.splice(0, 1);                                    // Delete any "none" that we might have
        if (list[0] && list[0].length < 4) fNormalized[3] = list[0].toUpperCase();      // Finally get the STYLE
        return fNormalized.join(";");
    }


    function normalizeAlign(a) {
        if (!a) return "--";

        var a = replaceAllMultiple(a.toString(), "  ", " ").trim().toUpperCase().split(" ");
        if (a.length == 0) return "--";
        if (a.length == 1) a[1] = "-";
        return a[0].charAt(0) + a[1].charAt(0) + "--";
    }

    function normalizeBorders(b) {
        b = replaceAllMultiple(b, "  ", " ").trim();
        var l = (b + ",NONE,NONE,NONE,NONE").split(",");
        var p = "";
        for (var i = 0; i < 4; i++) {
            l[i] = l[i].trim().toUpperCase();
            l[i] = ((l[i].substring(0, 4) == "NONE" ? "NONE" : l[i]).trim() + " NONE NONE NONE").trim();
            var st = l[i].split(" ");
            if (st[0].charAt(0) == "#") {
                st[2] = st[0]; st[0] = st[1]; st[1] = st[2];
            }
            p = p + st[0] + " " + st[1] + ",";
        }
        return p;
    }



    function createStyleSheet(defaultFont) {
        var styles = [], fonts = [], formats = BuiltInFormats.slice(0), borders = [], fills = [];

        var oStyles = {
            add: function (a) {
                var style = {};
                if (a.fill && a.fill.charAt(0) == "#") style.fill = 2 + findOrAdd(fills, a.fill.toString().substring(1).toUpperCase());                  // If there is a fill color add it, with a gap of 2, because of the TWO DEFAULT HARDCODED fills
                if (a.font) style.font = findOrAdd(fonts, normalizeFont(a.font.toString().trim()));
                if (a.format) style.format = findOrAdd(formats, a.format);
                if (a.align) style.align = normalizeAlign(a.align);
                if (a.border) style.border = 1 + findOrAdd(borders, normalizeBorders(a.border.toString().trim()));                                          // There is a HARDCODED border         
                return 1 + pushI(styles, style);                                                            // Add the style and return INDEX+1 because of the DEFAULT HARDCODED style
            }
        };

        if (!defaultFont) defaultFont="Calibri Light 12 0000EE";
        oStyles.add({ font: defaultFont });


        oStyles.register = function (thisOne) {
            for (var i = 0; i < styles.length; i++) {
                if (styles[i].font == thisOne.font && styles[i].format == thisOne.format && styles[i].fill == thisOne.fill && styles[i].border == thisOne.border && styles[i].align == thisOne.align) return i;
            }
            return pushI(styles, thisOne);
        }

        oStyles.getStyle = function (a) {
            return styles[a];
        }
        oStyles.toStyleSheet = function () {
            var s = '<?xml version="1.0" encoding="utf-8"?><styleSheet xmlns="http://schemas.openxmlformats.org/spreadsheetml/2006/main" ' +
                    'xmlns:mc="http://schemas.openxmlformats.org/markup-compatibility/2006" mc:Ignorable="x14ac" xmlns:x14ac="http://schemas.microsoft.com/office/spreadsheetml/2009/9/ac">';

            s = s + '<numFmts count="' + (formats.length - baseFormats) + '">';
            for (var i = baseFormats; i < formats.length; i++) s = s + '<numFmt numFmtId="' + (i) + '" formatCode="' + formats[i] + '"/>';
            s = s + '</numFmts>';


            s = s + '<fonts count="' + (fonts.length) + '" x14ac:knownFonts="1" xmlns:x14ac="http://schemas.microsoft.com/office/spreadsheetml/2009/9/ac">';
            for (var i = 0; i < fonts.length; i++) s = s + toFontXml(fonts[i]); //'<font><sz val="8" /><name val="Calibri" /><family val="2" /><scheme val="minor" /></font>' +
            s = s + '</fonts>';

            s = s + '<fills count="' + (2 + fills.length) + '"><fill><patternFill patternType="none"/></fill><fill><patternFill patternType="gray125"/></fill>';
            for (var i = 0; i < fills.length; i++) s = s + toFillXml(fills[i]);
            s = s + '</fills>';

            s = s + '<borders count="' + (1 + borders.length) + '"><border><left /><right /><top /><bottom /><diagonal /></border>';
            for (var i = 0; i < borders.length; i++) s = s + toBorderXml(borders[i]);
            s = s + '</borders>';

            s = s + '<cellStyleXfs count="1"><xf numFmtId="0" fontId="0" fillId="0" borderId="0"/></cellStyleXfs>';

            s = s + '<cellXfs count="' + (1 + styles.length) + '"><xf numFmtId="0" fontId="0" fillId="0" borderId="0" xfId="0" />';
            for (var i = 0; i < styles.length; i++) {
                s = s + toStyleXml(styles[i]);
            }
            s = s + '</cellXfs>';

            s = s + '<cellStyles count="1"><cellStyle name="Normal" xfId="0" builtinId="0"/></cellStyles>';
            s = s + '<dxfs count="0"/>';
            s = s + '</styleSheet>';
            return s;
        }
        return oStyles;
    }



    // --------------------- END Handling of styles





    var reUnescapedHtml = /[&<>"']/g, reHasUnescapedHtml = RegExp(reUnescapedHtml.source);
    var htmlEscapes = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#39;'
    };

    function basePropertyOf(object) {
        return function (key) {
            return object == null ? undefined : object[key];
        };
    }
    var escapeHtmlChar = basePropertyOf(htmlEscapes);

    function escape(string) {
        if (typeof string != 'string') string = null ? '' : (string + '');

        return (string && reHasUnescapedHtml.test(string))
          ? string.replace(reUnescapedHtml, escapeHtmlChar)
          : string;
    }
	
	function cellNameH(i) {
		var rest = Math.floor(i / 26) - 1; 
		var s = (rest > -1 ? cellNameH(rest) : '');
		return  s+ "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(i % 26); 
	}

    function cellName(colIndex, rowIndex) {
        return cellNameH(colIndex)+rowIndex;
    };

    function generateCell(cell, column, row) {
        var s = '<c r="' + cellName(column, row) + '"';
        if (cell.s) s = s + ' s="' + cell.s + '" ';
        if (isNaN(cell.v)) return s + ' t="inlineStr" ><is><t>' + escape(cell.v) + '</t></is></c>';
        return s + '><v>' + cell.v + '</v></c>';
    }

    function generateRow(row, index) {
        var rowIndex = index + 1;
        var oCells = [];
        for (var i = 0; i < row.cells.length; i++) {
            if (row.cells[i]) oCells.push(generateCell(row.cells[i], i, rowIndex));
        }
        var s = '<row r="' + rowIndex + '" '
        if (row.ht) s = s + ' ht="' + row.ht + '" customHeight="1" ';
        if (row.style) s = s + 's="' + row.style + '" customFormat="1"';
        return s + ' >' + oCells.join('') + '</row>';
    }


    function generateRows(rows) {
        var oRows = [];
        for (var index = 0; index < rows.length; index++) {
            if (rows[index]) {
                oRows.push(generateRow(rows[index], index));
            }
        }
        return oRows.join('');
    }

    function generateColums(columns) {
        if (columns.length == 0) return;

        var s = '<cols>';
        for (var i = 0; i < columns.length; i++) {
            var c = columns[i];
            if (c) {
                s = s + '<col min="' + (i + 1) + '" max="' + (i + 1) + '" ';
                if (c.wt == "auto") s = s + ' width="18" bestFit="1" customWidth="1" '; else if (c.wt) s = s + ' width="' + c.wt + '" customWidth="1" ';
                if (c.style) s = s + ' style="' + c.style + '"';
                s = s + "/>";
            }
        }
        return s + "</cols>";
    }


    function isObject(v) {
        return (v !== null && typeof v === 'object');
    }


    //  Loops all rows & columns in sheets. 
    //  If a row has a style it tries to apply the style componenets to all cells in the row (provided that the cell has not defined is not own style component)

    function CombineStyles(sheets, styles) {
        // First lets do the Rows
        for (var i = 0; i < sheets.length; i++) {
            // First let's do the rows
            for (var j = 0; j < sheets[i].rows.length; j++) {
                var row = sheets[i].rows[j];
                if (row && row.style) {
                    for (var k = 0; k < row.cells.length; k++) {
                        if (row.cells[k]) AddStyleToCell(row.cells[k], styles, row.style);
                    }
                }
            }

            // Second let's do the cols
            for (var c = 0; c < sheets[i].columns.length; c++) {
                if (sheets[i].columns[c] && sheets[i].columns[c].style) {
                    var cstyle = sheets[i].columns[c].style;
                    for (var j = 0; j < sheets[i].rows.length; j++) {
                        var row = sheets[i].rows[j];
                        if (row) for (var k = 0; k < row.cells.length; k++)
                            if (row.cells[k] && k == c) AddStyleToCell(row.cells[k], styles, cstyle);
                    }
                }
            }
        }
    }

    function AddStyleToCell(cell, styles, toAdd) {
        if (!cell) return;                                      // If no cell then return
        if (!cell.s) {                                          // If cell has no style, use toAdd
            cell.s = toAdd;
            return;
        }
        var cs = styles.getStyle(cell.s - 1);
        var os = styles.getStyle(toAdd - 1);
        var ns = {}, b = false;
        for (var x in cs) ns[x] = cs[x];                        // Clone cell style
        for (var x in os) {
            if (!ns[x]) {
                ns[x] = os[x];
                b = true;
            }
        }
        if (!b) return;                                         // If the toAdd style does NOT add anything new
        cell.s = 1 + styles.register(ns);
    }


    $JExcel.new = function (defaultFont) {
        var excel = {};

        var sheets = createSheets();                                                                              //  Create Excel    sheets
        var styles = createStyleSheet(defaultFont);                                                                        //  Create Styles   sheet
        sheets.add("Sheet 0");                                                                                  // At least we have a [Sheet 0]

        excel.addSheet = function (name) {
            if (!name) name = "Sheet " + sheets.length;
            return sheets.add(name);
        }


        excel.addStyle = function (a) {
            return styles.add(a);
        }

        excel.set = function (s, column, row, value, style) {
            if (isObject(s)) return this.set(s.sheet, s.column, s.row, s.value, s.style);                                           // If using Object form, expand it
            if (!s) s = 0;                                                                                                          // Use default sheet
            s = sheets.get(s);
            if (isNaN(column) && isNaN(row)) return s.set(value, style);                                                            // If this is a sheet operation
            if (!isNaN(column)) {                                                                                                    // If this is a column operation
                if (!isNaN(row)) return setCell(s.getCell(column, row), value, style);                                                // and also a ROW operation the this is a CELL operation
                return setColumn(s.getColumn(column), value, style);                                                                // if not we confirm than this is a COLUMN operation
            }
            return setRow(s.getRow(row), value, style);                                                                             // If got here, thet this is a Row operation
        }

        excel.generate = function (filename) {
            CombineStyles(sheets.sheets, styles);
            var zip = new JSZip();                                                                              // Create a ZIP file
            zip.file('_rels/.rels', sheets.toRels());                                                           // Add WorkBook RELS   
            zip.file('[Content_Types].xml', sheets.toContentType());                                            // Add content types
            var xl = zip.folder('xl');                                                                          // Add a XL folder for sheets
            xl.file('workbook.xml', sheets.toWorkBook());                                                       // And a WorkBook
            xl.file('_rels/workbook.xml.rels', sheets.toWorkBookRels());                                        // Add WorkBook RELs
            sheets.fileData(xl);                                                                                // Zip the rest    
            xl.file('styles.xml', styles.toStyleSheet());                                                       // Add styles
			var content = zip.generate({ type: "base64" });
			jArchi.fs.writeFile(filename, content, "BASE64");

        }
        return excel;
    }
})();