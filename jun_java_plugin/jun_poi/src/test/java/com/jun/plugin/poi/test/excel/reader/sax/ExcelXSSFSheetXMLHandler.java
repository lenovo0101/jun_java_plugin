/* ====================================================================
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
==================================================================== */
package com.jun.plugin.poi.test.excel.reader.sax;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.POILogger;
import org.apache.poi.xssf.model.CommentsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFComment;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTComment;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.jun.plugin.poi.test.excel.exception.BingSaxReadStopException;
import com.jun.plugin.poi.test.excel.reader.usermodel.ExcelBuiltinFormats;
import com.jun.plugin.poi.test.excel.reader.usermodel.ExcelDataFormatter;

/**
 * This class handles the processing of a sheet#.xml sheet part of a XSSF .xlsx
 * file, and generates row and cell events for it.
 */
public class ExcelXSSFSheetXMLHandler extends DefaultHandler {
	private static final POILogger logger = POILogFactory
			.getLogger(ExcelXSSFSheetXMLHandler.class);

	/**
	 * These are the different kinds of cells we support. We keep track of the
	 * current one between the start and end.
	 */
	enum xssfDataType {
		BOOLEAN, ERROR, FORMULA, INLINE_STRING, SST_STRING, NUMBER,
	}

	/**
	 * Table with the styles used for formatting
	 */
	private StylesTable stylesTable;

	/**
	 * Table with cell comments
	 */
	private CommentsTable commentsTable;

	/**
	 * Read only access to the shared strings table, for looking up (most)
	 * string cell's contents
	 */
	private ExcelReadOnlySharedStringsTable sharedStringsTable;

	/**
	 * Where our text is going
	 */
	private final BingSheetContentsHandler output;

	// Set when V start element is seen
	private boolean vIsOpen;
	// Set when F start element is seen
	private boolean fIsOpen;
	// Set when an Inline String "is" is seen
	private boolean isIsOpen;
	// Set when a header/footer element is seen
	private boolean hfIsOpen;

	// Set when cell start element is seen;
	// used when cell close element is seen.
	private xssfDataType nextDataType;

	// Used to format numeric cell values.
	private short formatIndex;
	private String formatString;
	private final ExcelDataFormatter formatter;
	private int rowNum;
	private int nextRowNum; // some sheets do not have rowNums, Excel can read
							// them so we should try to handle them correctly as
							// well
	private String cellRef;
	private boolean formulasNotResults;

	// Gathers characters as they are seen.
	private StringBuffer value = new StringBuffer();
	private StringBuffer formula = new StringBuffer();
	private StringBuffer headerFooter = new StringBuffer();

	private Queue<CellReference> commentCellRefs;

	/**
	 * Accepts objects needed while parsing.
	 * 
	 * @param styles
	 *            Table of styles
	 * @param strings
	 *            Table of shared strings
	 */
	public ExcelXSSFSheetXMLHandler(StylesTable styles, CommentsTable comments,
			ExcelReadOnlySharedStringsTable strings,
			BingSheetContentsHandler sheetContentsHandler,
			ExcelDataFormatter dataFormatter, boolean formulasNotResults) {
		this.stylesTable = styles;
		this.commentsTable = comments;
		this.sharedStringsTable = strings;
		this.output = sheetContentsHandler;
		this.formulasNotResults = formulasNotResults;
		this.nextDataType = xssfDataType.NUMBER;
		this.formatter = dataFormatter;
		init();
	}

	/**
	 * Accepts objects needed while parsing.
	 * 
	 * @param styles
	 *            Table of styles
	 * @param strings
	 *            Table of shared strings
	 */
	public ExcelXSSFSheetXMLHandler(StylesTable styles,
			ExcelReadOnlySharedStringsTable strings,
			BingSheetContentsHandler sheetContentsHandler,
			ExcelDataFormatter dataFormatter, boolean formulasNotResults) {
		this(styles, null, strings, sheetContentsHandler, dataFormatter,
				formulasNotResults);
	}

	/**
	 * Accepts objects needed while parsing.
	 * 
	 * @param styles
	 *            Table of styles
	 * @param strings
	 *            Table of shared strings
	 */
	public ExcelXSSFSheetXMLHandler(StylesTable styles,
			ExcelReadOnlySharedStringsTable strings,
			BingSheetContentsHandler sheetContentsHandler,
			boolean formulasNotResults) {
		this(styles, strings, sheetContentsHandler, new ExcelDataFormatter(),
				formulasNotResults);
	}

	@SuppressWarnings("deprecation")
	private void init() {
		if (commentsTable != null) {
			commentCellRefs = new LinkedList<CellReference>();
			for (CTComment comment : commentsTable.getCTComments()
					.getCommentList().getCommentArray()) {
				commentCellRefs.add(new CellReference(comment.getRef()));
			}
		}
	}

	private boolean isTextTag(String name) {
		if ("v".equals(name)) {
			// Easy, normal v text tag
			return true;
		}
		if ("inlineStr".equals(name)) {
			// Easy inline string
			return true;
		}
		if ("t".equals(name) && isIsOpen) {
			// Inline string <is><t>...</t></is> pair
			return true;
		}
		// It isn't a text tag
		return false;
	}

	@Override
	@SuppressWarnings("unused")
	public void startElement(String uri, String localName, String name,
			Attributes attributes) throws SAXException {

		if (isTextTag(name)) {
			vIsOpen = true;
			// Clear contents cache
			value.setLength(0);
		} else if ("is".equals(name)) {
			// Inline string outer tag
			isIsOpen = true;
		} else if ("f".equals(name)) {
			// Clear contents cache
			formula.setLength(0);

			// Mark us as being a formula if not already
			if (nextDataType == xssfDataType.NUMBER) {
				nextDataType = xssfDataType.FORMULA;
			}

			// Decide where to get the formula string from
			String type = attributes.getValue("t");
			if (type != null && type.equals("shared")) {
				// Is it the one that defines the shared, or uses it?
				String ref = attributes.getValue("ref");
				String si = attributes.getValue("si");

				if (ref != null) {
					// This one defines it
					// TODO Save it somewhere
					fIsOpen = true;
				} else {
					// This one uses a shared formula
					// TODO Retrieve the shared formula and tweak it to
					// match the current cell
					if (formulasNotResults) {
						logger.log(POILogger.WARN,
								"shared formulas not yet supported!");
					} else {
						// It's a shared formula, so we can't get at the formula
						// string yet
						// However, they don't care about the formula string, so
						// that's ok!
					}
				}
			} else {
				fIsOpen = true;
			}
		} else if ("oddHeader".equals(name) || "evenHeader".equals(name)
				|| "firstHeader".equals(name) || "firstFooter".equals(name)
				|| "oddFooter".equals(name) || "evenFooter".equals(name)) {
			hfIsOpen = true;
			// Clear contents cache
			headerFooter.setLength(0);
		} else if ("row".equals(name)) {
			String rowNumStr = attributes.getValue("r");
			if (rowNumStr != null) {
				rowNum = Integer.parseInt(rowNumStr) - 1;
			} else {
				rowNum = nextRowNum;
			}
			output.startRow(rowNum);
		}
		// c => cell
		else if ("c".equals(name)) {
			// Set up defaults.
			this.nextDataType = xssfDataType.NUMBER;
			this.formatIndex = -1;
			this.formatString = null;
			cellRef = attributes.getValue("r");
			String cellType = attributes.getValue("t");
			String cellStyleStr = attributes.getValue("s");
			if ("b".equals(cellType))
				nextDataType = xssfDataType.BOOLEAN;
			else if ("e".equals(cellType))
				nextDataType = xssfDataType.ERROR;
			else if ("inlineStr".equals(cellType))
				nextDataType = xssfDataType.INLINE_STRING;
			else if ("s".equals(cellType))
				nextDataType = xssfDataType.SST_STRING;
			else if ("str".equals(cellType))
				nextDataType = xssfDataType.FORMULA;
			else {
				// Number, but almost certainly with a special style or format
				XSSFCellStyle style = null;
				if (cellStyleStr != null) {
					int styleIndex = Integer.parseInt(cellStyleStr);
					style = stylesTable.getStyleAt(styleIndex);
				} else if (stylesTable.getNumCellStyles() > 0) {
					style = stylesTable.getStyleAt(0);
				}
				if (style != null) {
					this.formatIndex = style.getDataFormat();
					this.formatString = style.getDataFormatString();
					if (this.formatString == null
							|| this.formatString.startsWith("reserved-0x"))
						this.formatString = ExcelBuiltinFormats
								.getBuiltinFormat(this.formatIndex);
				}
			}
		}
	}

	@Override
	public void endElement(String uri, String localName, String name)
			throws SAXException {
		String thisStr = null;

		// v => contents of a cell
		if (isTextTag(name)) {
			vIsOpen = false;

			// Process the value contents as required, now we have it all
			switch (nextDataType) {
			case BOOLEAN:
				char first = value.charAt(0);
				thisStr = first == '0' ? "FALSE" : "TRUE";
				break;

			case ERROR:
				thisStr = "ERROR:" + value.toString();
				break;

			case FORMULA:
				if (formulasNotResults) {
					thisStr = formula.toString();
				} else {
					String fv = value.toString();

					if (this.formatString != null) {
						try {
							// Try to use the value as a formattable number
							double d = Double.parseDouble(fv);
							thisStr = formatter.formatRawCellContents(d,
									this.formatIndex, this.formatString);
						} catch (NumberFormatException e) {
							// Formula is a String result not a Numeric one
							thisStr = fv;
						}
					} else {
						// No formating applied, just do raw value in all cases
						thisStr = fv;
					}
				}
				break;

			case INLINE_STRING:
				// TODO: Can these ever have formatting on them?
				XSSFRichTextString rtsi = new XSSFRichTextString(
						value.toString());
				thisStr = rtsi.toString();
				break;

			case SST_STRING:
				String sstIndex = value.toString();
				try {
					int idx = Integer.parseInt(sstIndex);
					XSSFRichTextString rtss = new XSSFRichTextString(
							sharedStringsTable.getEntryAt(idx));
					thisStr = rtss.toString();
				} catch (NumberFormatException ex) {
					logger.log(POILogger.ERROR, "Failed to parse SST index '"
							+ sstIndex, ex);
				}
				break;

			case NUMBER:
				String n = value.toString();
				if (this.formatString != null && n.length() > 0)
					thisStr = formatter.formatRawCellContents(
							Double.parseDouble(n), this.formatIndex,
							this.formatString);
				else
					thisStr = n;
				break;

			default:
				thisStr = "(TODO: Unexpected type: " + nextDataType + ")";
				break;
			}

			// Do we have a comment for this cell? ????????????
			//checkForEmptyCellComments(EmptyCellCommentsCheckType.CELL);
			XSSFComment comment = commentsTable != null ? commentsTable
					.findCellComment(cellRef) : null;

			// Output ??????????????????
			output.cell(rowNum, cellRef, thisStr, comment);
		} else if ("f".equals(name)) {
			fIsOpen = false;
		} else if ("is".equals(name)) {
			isIsOpen = false;
		} else if ("row".equals(name)) {
			// Handle any "missing" cells which had comments attached
			checkForEmptyCellComments(EmptyCellCommentsCheckType.END_OF_ROW);

			// Finish up the row
			output.endRow(rowNum);

			// some sheets do not have rowNum set in the XML, Excel can read
			// them so we should try to read them as well
			nextRowNum = rowNum + 1;
		} else if ("sheetData".equals(name)) {
			// Handle any "missing" cells which had comments attached
			checkForEmptyCellComments(EmptyCellCommentsCheckType.END_OF_SHEET_DATA);
		} else if ("oddHeader".equals(name) || "evenHeader".equals(name)
				|| "firstHeader".equals(name)) {
			hfIsOpen = false;
			output.headerFooter(headerFooter.toString(), true, name);
		} else if ("oddFooter".equals(name) || "evenFooter".equals(name)
				|| "firstFooter".equals(name)) {
			hfIsOpen = false;
			output.headerFooter(headerFooter.toString(), false, name);
		}
	}

	/**
	 * Captures characters only if a suitable element is open. Originally was
	 * just "v"; extended for inlineStr also.
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (vIsOpen) {
			value.append(ch, start, length);
		}
		if (fIsOpen) {
			formula.append(ch, start, length);
		}
		if (hfIsOpen) {
			headerFooter.append(ch, start, length);
		}
	}

	/**
	 * Do a check for, and output, comments in otherwise empty cells.
	 * 
	 * @throws BingSaxReadStopException
	 */
	private void checkForEmptyCellComments(EmptyCellCommentsCheckType type)
			throws BingSaxReadStopException {
		if (commentCellRefs != null && !commentCellRefs.isEmpty()) {
			// If we've reached the end of the sheet data, output any
			// comments we haven't yet already handled
			if (type == EmptyCellCommentsCheckType.END_OF_SHEET_DATA) {
				while (!commentCellRefs.isEmpty()) {
					outputEmptyCellComment(commentCellRefs.remove());
				}
				return;
			}

			// At the end of a row, handle any comments for "missing" rows
			// before us
			if (this.cellRef == null) {
				if (type == EmptyCellCommentsCheckType.END_OF_ROW) {
					while (!commentCellRefs.isEmpty()) {
						if (commentCellRefs.peek().getRow() == rowNum) {
							outputEmptyCellComment(commentCellRefs.remove());
						} else {
							return;
						}
					}
					return;
				} else {
					throw new IllegalStateException(
							"Cell ref should be null only if there are only empty cells in the row; rowNum: "
									+ rowNum);
				}
			}

			CellReference nextCommentCellRef;
			do {
				CellReference cellRef = new CellReference(this.cellRef);
				CellReference peekCellRef = commentCellRefs.peek();
				if (type == EmptyCellCommentsCheckType.CELL
						&& cellRef.equals(peekCellRef)) {
					// remove the comment cell ref from the list if we're about
					// to handle it alongside the cell content
					commentCellRefs.remove();
					return;
				} else {
					// fill in any gaps if there are empty cells with comment
					// mixed in with non-empty cells
					int comparison = cellRefComparator.compare(peekCellRef,
							cellRef);
					if (comparison > 0
							&& type == EmptyCellCommentsCheckType.END_OF_ROW
							&& peekCellRef.getRow() <= rowNum) {
						nextCommentCellRef = commentCellRefs.remove();
						outputEmptyCellComment(nextCommentCellRef);
					} else if (comparison < 0
							&& type == EmptyCellCommentsCheckType.CELL
							&& peekCellRef.getRow() <= rowNum) {
						nextCommentCellRef = commentCellRefs.remove();
						outputEmptyCellComment(nextCommentCellRef);
					} else {
						nextCommentCellRef = null;
					}
				}
			} while (nextCommentCellRef != null && !commentCellRefs.isEmpty());
		}
	}

	/**
	 * Output an empty-cell comment.
	 * 
	 * @throws BingSaxReadStopException
	 */
	private void outputEmptyCellComment(CellReference cellRef)
			throws BingSaxReadStopException {
		String cellRefString = cellRef.formatAsString();
		XSSFComment comment = commentsTable.findCellComment(cellRefString);
		output.cell(rowNum, cellRefString, null, comment);
	}

	private enum EmptyCellCommentsCheckType {
		CELL, END_OF_ROW, END_OF_SHEET_DATA
	}

	private static final Comparator<CellReference> cellRefComparator = new Comparator<CellReference>() {
		@Override
		public int compare(CellReference o1, CellReference o2) {
			int result = compare(o1.getRow(), o2.getRow());
			if (result == 0) {
				result = compare(o1.getCol(), o2.getCol());
			}
			return result;
		}

		public int compare(int x, int y) {
			return (x < y) ? -1 : ((x == y) ? 0 : 1);
		}
	};

	public void ignoreNumFormat(boolean b) {
		this.formatter.setIgnoreNumFormat(b);
	}
	
	/**
	 * You need to implement this to handle the results of the sheet parsing.
	 */
	public interface BingSheetContentsHandler {
		/** A row with the (zero based) row number has started */
		public void startRow(int rowNum)throws BingSaxReadStopException;

		/** A row with the (zero based) row number has ended */
		public void endRow(int rowNum);

		/**
		 * A cell, with the given formatted value (may be null), and possibly a
		 * comment (may be null), was encountered
		 */
		public void cell(int rowNum, String cellReference,
				String formattedValue, XSSFComment comment)
				;

		/** A header or footer has been encountered */
		public void headerFooter(String text, boolean isHeader, String tagName);
	}

	
}
