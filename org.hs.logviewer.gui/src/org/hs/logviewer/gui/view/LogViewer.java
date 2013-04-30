package org.hs.logviewer.gui.view;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.coordinate.Range;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.filterrow.DefaultGlazedListsFilterStrategy;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowHeaderComposite;
import org.eclipse.nebula.widgets.nattable.grid.GridRegion;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultColumnHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultCornerDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.data.DefaultRowHeaderDataProvider;
import org.eclipse.nebula.widgets.nattable.grid.layer.ColumnHeaderLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.CornerLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultColumnHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.DefaultRowHeaderDataLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.GridLayer;
import org.eclipse.nebula.widgets.nattable.grid.layer.RowHeaderLayer;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayerListener;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.event.ILayerEvent;
import org.eclipse.nebula.widgets.nattable.layer.stack.DefaultBodyLayerStack;
import org.eclipse.nebula.widgets.nattable.selection.event.CellSelectionEvent;
import org.eclipse.nebula.widgets.nattable.selection.event.RowSelectionEvent;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.hs.logviewer.gui.view.internal.BodyLayerStack;
import org.hs.logviewer.gui.view.internal.FilterRowCustomConfiguration;
import org.hs.logviewer.parser.IMessagePart;
import org.hs.logviewer.parser.ParserEngine;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;

public class LogViewer extends ViewPart {

	private static final String FILE_NAME = "D:\\scratch\\tmp\\testTrcException.txt";

	private static String PATTERN = "%d \\{[%t]\\} %c %l - %m";

	private ParserEngine engine;
	private ListDataProvider<IMessagePart> bodyDataProvider;
	private BodyLayerStack bodyLayer;

	private NatTable table;

	private IColumnAccessor<IMessagePart> columnAccessor;

	private Text msgField;

	public LogViewer() {
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new GridLayout());
		table = createTable(parent);
		{
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			table.setLayoutData(gd);
		}
		msgField = new Text(parent, SWT.BORDER | SWT.MULTI);
		{
			GridData gd = new GridData(SWT.FILL, SWT.TOP, true, false);
			gd.heightHint = 75;
			msgField.setLayoutData(gd);
		}
	}

	public NatTable createTable(Composite parent) {
		IConfigRegistry configRegistry = new ConfigRegistry();
		bodyDataProvider = setupBodyDataProvider();
		String[] propertyNames = new String[] { "date", "thread", "level", "logger", "message" };
		Map<String, String> propertyToLabels = new HashMap<String, String>();
		propertyToLabels.put("date", "Date");
		propertyToLabels.put("thread", "Thread");
		propertyToLabels.put("level", "Level");
		propertyToLabels.put("logger", "Logger");
		propertyToLabels.put("message", "Message");

		// Underlying data source
		EventList<IMessagePart> eventList = GlazedLists.eventList(engine.getParsedMessages());
		FilterList<IMessagePart> filterList = new FilterList<IMessagePart>(eventList);

		bodyDataProvider = new ListDataProvider<IMessagePart>(filterList, columnAccessor);
		DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);
		DefaultBodyLayerStack bodyLayer = new DefaultBodyLayerStack(bodyDataLayer);
		ColumnOverrideLabelAccumulator bodyLabelAccumulator = new ColumnOverrideLabelAccumulator(bodyDataLayer);
		bodyDataLayer.setConfigLabelAccumulator(bodyLabelAccumulator);

		// Column header layer
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(propertyNames, propertyToLabels);
		DataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(columnHeaderDataProvider);
		ColumnHeaderLayer columnHeaderLayer = new ColumnHeaderLayer(columnHeaderDataLayer, bodyLayer,
				bodyLayer.getSelectionLayer());
		// Note: The column header layer is wrapped in a filter row composite.
		// This plugs in the filter row functionality
		CompositeMatcherEditor<IMessagePart> autoFilterMatcherEditor = new CompositeMatcherEditor<IMessagePart>();
		filterList.setMatcherEditor(autoFilterMatcherEditor);

		FilterRowHeaderComposite<IMessagePart> filterRowHeaderLayer = new FilterRowHeaderComposite<IMessagePart>(
				new DefaultGlazedListsFilterStrategy<IMessagePart>(autoFilterMatcherEditor, columnAccessor,
						configRegistry), columnHeaderLayer, columnHeaderDataProvider, configRegistry);

		ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(columnHeaderDataLayer);
		columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);

		// Row header layer
		DefaultRowHeaderDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(bodyDataProvider);
		DefaultRowHeaderDataLayer rowHeaderDataLayer = new DefaultRowHeaderDataLayer(rowHeaderDataProvider);
		RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer, bodyLayer, bodyLayer.getSelectionLayer());

		// Corner layer
		DefaultCornerDataProvider cornerDataProvider = new DefaultCornerDataProvider(columnHeaderDataProvider,
				rowHeaderDataProvider);
		DataLayer cornerDataLayer = new DataLayer(cornerDataProvider);
		CornerLayer cornerLayer = new CornerLayer(cornerDataLayer, rowHeaderLayer, filterRowHeaderLayer);

		GridLayer gridLayer = new GridLayer(bodyLayer, filterRowHeaderLayer, rowHeaderLayer, cornerLayer);

		// Custom selection configuration
		// SelectionLayer selectionLayer = bodyLayer.getSelectionLayer();
		// selectionLayer.setSelectionModel(new
		// RowSelectionModel(selectionLayer, bodyDataProvider,
		// new IRowIdAccessor<IMessagePart>() {
		//
		// @Override
		// public Serializable getRowId(IMessagePart rowObject) {
		// return null;
		// }
		//
		// }));

		NatTable natTable = new NatTable(parent, gridLayer, false);
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.addConfiguration(new HeaderMenuConfiguration(natTable));
		natTable.addConfiguration(new FilterRowCustomConfiguration(engine) {
			@Override
			public void configureRegistry(IConfigRegistry configRegistry) {
				super.configureRegistry(configRegistry);

				// Shade the row to be slightly darker than the blue background.
				final Style rowStyle = new Style();
				rowStyle.setAttributeValue(CellStyleAttributes.BACKGROUND_COLOR, GUIHelper.getColor(197, 212, 231));
				configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, rowStyle, DisplayMode.NORMAL,
						GridRegion.FILTER_ROW);
			}
		});
		natTable.addLayerListener(new ILayerListener() {

			// Default selection behavior selects cells by default.
			@Override
			public void handleLayerEvent(ILayerEvent event) {
				int pos = -1;
				if (event instanceof CellSelectionEvent) {
					CellSelectionEvent cellEvent = (CellSelectionEvent) event;
					pos = cellEvent.getRowPosition();
					// log("Selected cell: ["
					// + cellEvent.getRowPosition()
					// + ", "
					// + cellEvent.getColumnPosition()
					// + "], "
					// + natTable.getDataValueByPosition(
					// cellEvent.getColumnPosition(),
					// cellEvent.getRowPosition()));
				} else if (event instanceof RowSelectionEvent) {
					RowSelectionEvent rowEvent = (RowSelectionEvent) event;
					Collection<Range> ranges = rowEvent.getRowPositionRanges();
					if (ranges.size() == 1) {
						Range range = ranges.iterator().next();
						System.err.println(range);
						// pos = range.end;
						// pos = range.
					}
				}
				if (-1 != pos) {
					String message = engine.getParsedMessages().get(pos - 2).getValue(4);
					msgField.setText(message);
				} else {
					msgField.setText("");
				}
			}
		});

		Style style = new Style();
		style.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT, HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE, style, DisplayMode.NORMAL);

		natTable.setConfigRegistry(configRegistry);
		natTable.configure();
		return natTable;
	}

	@Override
	public void setFocus() {
		table.setFocus();
	}

	private ListDataProvider<IMessagePart> setupBodyDataProvider() {
		engine = new ParserEngine(PATTERN, new File(FILE_NAME));
		columnAccessor = new IColumnAccessor<IMessagePart>() {

			@Override
			public int getColumnCount() {
				// TODO get from Pattern
				return 5;
			}

			@Override
			public Object getDataValue(IMessagePart rowObject, int columnIndex) {
				return rowObject.getValue(columnIndex);
			}

			@Override
			public void setDataValue(IMessagePart rowObject, int columnIndex, Object newValue) {
				throw new UnsupportedOperationException();
			}
		};

		return new ListDataProvider(engine.getParsedMessages(), columnAccessor);

	}
}
