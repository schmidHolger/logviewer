package org.hs.logviewer.gui.view;

import java.io.File;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.nebula.widgets.nattable.NatTable;
import org.eclipse.nebula.widgets.nattable.config.AbstractRegistryConfiguration;
import org.eclipse.nebula.widgets.nattable.config.CellConfigAttributes;
import org.eclipse.nebula.widgets.nattable.config.ConfigRegistry;
import org.eclipse.nebula.widgets.nattable.config.DefaultNatTableStyleConfiguration;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.data.IColumnAccessor;
import org.eclipse.nebula.widgets.nattable.data.IDataProvider;
import org.eclipse.nebula.widgets.nattable.data.ListDataProvider;
import org.eclipse.nebula.widgets.nattable.data.convert.DefaultDoubleDisplayConverter;
import org.eclipse.nebula.widgets.nattable.extension.glazedlists.filterrow.DefaultGlazedListsFilterStrategy;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowDataLayer;
import org.eclipse.nebula.widgets.nattable.filterrow.FilterRowHeaderComposite;
import org.eclipse.nebula.widgets.nattable.filterrow.config.FilterRowConfigAttributes;
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
import org.eclipse.nebula.widgets.nattable.hideshow.ColumnHideShowLayer;
import org.eclipse.nebula.widgets.nattable.layer.AbstractLayerTransform;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;
import org.eclipse.nebula.widgets.nattable.layer.cell.ColumnOverrideLabelAccumulator;
import org.eclipse.nebula.widgets.nattable.layer.stack.DefaultBodyLayerStack;
import org.eclipse.nebula.widgets.nattable.reorder.ColumnReorderLayer;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.DisplayMode;
import org.eclipse.nebula.widgets.nattable.style.HorizontalAlignmentEnum;
import org.eclipse.nebula.widgets.nattable.style.Style;
import org.eclipse.nebula.widgets.nattable.ui.menu.HeaderMenuConfiguration;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;
import org.eclipse.nebula.widgets.nattable.viewport.ViewportLayer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.hs.logviewer.parser.IMessagePart;
import org.hs.logviewer.parser.ParserEngine;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.FilterList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.matchers.CompositeMatcherEditor;

public class LogViewer extends ViewPart {
	public static class BodyLayerStack extends AbstractLayerTransform {

		private final SelectionLayer selectionLayer;

		public BodyLayerStack(IDataProvider dataProvider) {
			DataLayer bodyDataLayer = new DataLayer(dataProvider);
			ColumnReorderLayer columnReorderLayer = new ColumnReorderLayer(
					bodyDataLayer);
			ColumnHideShowLayer columnHideShowLayer = new ColumnHideShowLayer(
					columnReorderLayer);
			selectionLayer = new SelectionLayer(columnHideShowLayer);
			ViewportLayer viewportLayer = new ViewportLayer(selectionLayer);
			setUnderlyingLayer(viewportLayer);
		}

		public SelectionLayer getSelectionLayer() {
			return selectionLayer;
		}
	}

	public class ColumnHeaderLayerStack extends AbstractLayerTransform {

		public ColumnHeaderLayerStack(IDataProvider dataProvider) {
			DataLayer dataLayer = new DataLayer(dataProvider);
			ColumnHeaderLayer colHeaderLayer = new ColumnHeaderLayer(dataLayer,
					bodyLayer, bodyLayer.getSelectionLayer());
			setUnderlyingLayer(colHeaderLayer);
		}
	}

	public static class FilterRowCustomConfiguration extends
			AbstractRegistryConfiguration {

		final DefaultDoubleDisplayConverter doubleDisplayConverter = new DefaultDoubleDisplayConverter();

		@Override
		public void configureRegistry(IConfigRegistry configRegistry) {

			configRegistry.registerConfigAttribute(
					FilterRowConfigAttributes.FILTER_COMPARATOR,
					getIngnorecaseComparator(), DisplayMode.NORMAL,
					FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 0);

			// Thread
			// configRegistry.registerConfigAttribute(
			// EditConfigAttributes.CELL_EDITOR,
			// new ComboBoxCellEditor(Arrays.asList(new PricingTypeBean(
			// "MN"), new PricingTypeBean("AT"))),
			// DisplayMode.NORMAL,
			// FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 1);

			// Type
			// configRegistry.registerConfigAttribute(
			// EditConfigAttributes.CELL_EDITOR,
			// new ComboBoxCellEditor(Arrays.asList(new PricingTypeBean(
			// "MN"), new PricingTypeBean("AT"))),
			// DisplayMode.NORMAL,
			// FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 2);

			// Logger
			// configRegistry.registerConfigAttribute(
			// EditConfigAttributes.CELL_EDITOR,
			// new ComboBoxCellEditor(Arrays.asList(new PricingTypeBean(
			// "MN"), new PricingTypeBean("AT"))),
			// DisplayMode.NORMAL,
			// FilterRowDataLayer.FILTER_ROW_COLUMN_LABEL_PREFIX + 2);
		}
	};

	public class RowHeaderLayerStack extends AbstractLayerTransform {

		public RowHeaderLayerStack(IDataProvider dataProvider) {
			DataLayer dataLayer = new DataLayer(dataProvider, 50, 20);
			RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(dataLayer,
					bodyLayer, bodyLayer.getSelectionLayer());
			setUnderlyingLayer(rowHeaderLayer);
		}
	}

	private static final String FILE_NAME = "D:\\scratch\\tmp\\testTrcException.txt";

	private static String PATTERN = "%d \\{[%t]\\} %l - %m";

	private static Comparator<String> getIngnorecaseComparator() {
		return new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}
		};
	}

	private ParserEngine engine;
	private IDataProvider bodyDataProvider;
	private BodyLayerStack bodyLayer;

	private NatTable table;

	private IColumnAccessor<IMessagePart> columnAccessor;

	public LogViewer() {
	}

	@Override
	public void createPartControl(Composite parent) {
		table = createTable(parent);
	}

	public NatTable createTable(Composite parent) {
		IConfigRegistry configRegistry = new ConfigRegistry();
		bodyDataProvider = setupBodyDataProvider();
		String[] propertyNames = new String[] { "date", "thread", "level",
				"message" };
		Map<String, String> propertyToLabels = new HashMap<String, String>();
		propertyToLabels.put("date", "Date");
		propertyToLabels.put("thread", "Thread");
		propertyToLabels.put("level", "Level");
		propertyToLabels.put("message", "Message");

		// Underlying data source
		EventList<IMessagePart> eventList = GlazedLists.eventList(engine
				.parse());
		FilterList<IMessagePart> filterList = new FilterList<IMessagePart>(
				eventList);

		// Body layer
		// IColumnPropertyAccessor<IMessagePart> columnPropertyAccessor = new
		// ReflectiveColumnPropertyAccessor<IMessagePart>(
		// propertyNames);
		bodyDataProvider = new ListDataProvider<IMessagePart>(filterList,
				columnAccessor);
		DataLayer bodyDataLayer = new DataLayer(bodyDataProvider);
		DefaultBodyLayerStack bodyLayer = new DefaultBodyLayerStack(
				bodyDataLayer);
		ColumnOverrideLabelAccumulator bodyLabelAccumulator = new ColumnOverrideLabelAccumulator(
				bodyDataLayer);
		bodyDataLayer.setConfigLabelAccumulator(bodyLabelAccumulator);

		// bodyLabelAccumulator
		// .registerColumnOverrides(
		// RowDataListFixture
		// .getColumnIndexOfProperty(RowDataListFixture.PRICING_TYPE_PROP_NAME),
		// "PRICING_TYPE_PROP_NAME");

		// Column header layer
		IDataProvider columnHeaderDataProvider = new DefaultColumnHeaderDataProvider(
				propertyNames, propertyToLabels);
		DataLayer columnHeaderDataLayer = new DefaultColumnHeaderDataLayer(
				columnHeaderDataProvider);
		ColumnHeaderLayer columnHeaderLayer = new ColumnHeaderLayer(
				columnHeaderDataLayer, bodyLayer, bodyLayer.getSelectionLayer());
		// Note: The column header layer is wrapped in a filter row composite.
		// This plugs in the filter row functionality
		CompositeMatcherEditor<IMessagePart> autoFilterMatcherEditor = new CompositeMatcherEditor<IMessagePart>();
		filterList.setMatcherEditor(autoFilterMatcherEditor);

		FilterRowHeaderComposite<IMessagePart> filterRowHeaderLayer = new FilterRowHeaderComposite<IMessagePart>(
				new DefaultGlazedListsFilterStrategy<IMessagePart>(
						autoFilterMatcherEditor, columnAccessor, configRegistry),
				columnHeaderLayer, columnHeaderDataProvider, configRegistry);

		ColumnOverrideLabelAccumulator labelAccumulator = new ColumnOverrideLabelAccumulator(
				columnHeaderDataLayer);
		columnHeaderDataLayer.setConfigLabelAccumulator(labelAccumulator);

		// Register labels
		// labelAccumulator.registerColumnOverrides(
		// RowDataListFixture.getColumnIndexOfProperty(RowDataListFixture.RATING_PROP_NAME),
		// "CUSTOM_COMPARATOR_LABEL");

		// Row header layer
		DefaultRowHeaderDataProvider rowHeaderDataProvider = new DefaultRowHeaderDataProvider(
				bodyDataProvider);
		DefaultRowHeaderDataLayer rowHeaderDataLayer = new DefaultRowHeaderDataLayer(
				rowHeaderDataProvider);
		RowHeaderLayer rowHeaderLayer = new RowHeaderLayer(rowHeaderDataLayer,
				bodyLayer, bodyLayer.getSelectionLayer());

		// Corner layer
		DefaultCornerDataProvider cornerDataProvider = new DefaultCornerDataProvider(
				columnHeaderDataProvider, rowHeaderDataProvider);
		DataLayer cornerDataLayer = new DataLayer(cornerDataProvider);
		CornerLayer cornerLayer = new CornerLayer(cornerDataLayer,
				rowHeaderLayer, filterRowHeaderLayer);

		GridLayer gridLayer = new GridLayer(bodyLayer, filterRowHeaderLayer,
				rowHeaderLayer, cornerLayer);

		NatTable natTable = new NatTable(parent, gridLayer, false);
		natTable.addConfiguration(new DefaultNatTableStyleConfiguration());
		natTable.addConfiguration(new HeaderMenuConfiguration(natTable));
		natTable.addConfiguration(new FilterRowCustomConfiguration() {
			@Override
			public void configureRegistry(IConfigRegistry configRegistry) {
				super.configureRegistry(configRegistry);

				// Shade the row to be slightly darker than the blue background.
				final Style rowStyle = new Style();
				rowStyle.setAttributeValue(
						CellStyleAttributes.BACKGROUND_COLOR,
						GUIHelper.getColor(197, 212, 231));
				configRegistry.registerConfigAttribute(
						CellConfigAttributes.CELL_STYLE, rowStyle,
						DisplayMode.NORMAL, GridRegion.FILTER_ROW);
			}
		});

		Style style = new Style();
		// style.setAttributeValue(CellStyleAttributes.FOREGROUND_COLOR,
		// GUIHelper.COLOR_BLUE);
		style.setAttributeValue(CellStyleAttributes.HORIZONTAL_ALIGNMENT,
				HorizontalAlignmentEnum.LEFT);
		configRegistry.registerConfigAttribute(CellConfigAttributes.CELL_STYLE,
				style, DisplayMode.NORMAL);

		natTable.setConfigRegistry(configRegistry);
		natTable.configure();
		return natTable;
	}

	@Override
	public void setFocus() {
		table.setFocus();
	}

	private IDataProvider setupBodyDataProvider() {
		engine = new ParserEngine(PATTERN, new File(FILE_NAME));
		columnAccessor = new IColumnAccessor<IMessagePart>() {

			@Override
			public int getColumnCount() {
				// TODO get from Pattern
				return 4;
			}

			@Override
			public Object getDataValue(IMessagePart rowObject, int columnIndex) {
				return rowObject.getValue(columnIndex);
			}

			@Override
			public void setDataValue(IMessagePart rowObject, int columnIndex,
					Object newValue) {
				throw new UnsupportedOperationException();
			}
		};

		return new ListDataProvider(engine.parse(), columnAccessor);

	}
}
