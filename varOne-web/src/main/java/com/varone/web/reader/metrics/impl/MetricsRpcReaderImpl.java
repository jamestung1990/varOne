/**
 * 
 */
package com.varone.web.reader.metrics.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.varone.hadoop.rpc.protos.MetricsProtos.MetricsResponseProto.MetricsMapProto;
import com.varone.hadoop.rpc.protos.MetricsProtos.MetricsResponseProto.MetricsMapProto.TupleProto;
import com.varone.hadoop.rpc.protos.MetricsProtos.MetricsTypeProto;
import com.varone.node.utils.MetricsDataTransfer;
import com.varone.web.metrics.bean.MetricBean;
import com.varone.web.metrics.bean.NodeBean;
import com.varone.web.metrics.bean.TimeValuePairBean;
import com.varone.web.reader.metrics.MetricsReader;
import com.varone.web.reader.metrics.stub.MetricsStubCenter;

/**
 * @author allen
 *
 */
public class MetricsRpcReaderImpl implements MetricsReader {
	private MetricsStubCenter stubCenter;

	public MetricsRpcReaderImpl(String port) {
		this.stubCenter = new MetricsStubCenter(port);
	}
	
	public MetricsRpcReaderImpl(List<String> nodes, String port) {
		this.stubCenter = new MetricsStubCenter(nodes, port);
	}

	/* (non-Javadoc)
	 * @see com.varone.web.reader.metrics.MetricsReader#getNodeMetrics(java.lang.String, java.util.List)
	 */
	@Override
	public List<NodeBean> getAllNodeMetrics(String applicationId,
			List<String> metricsType) throws Exception {
		MetricsDataTransfer metricsTransfer = new MetricsDataTransfer();
		List<MetricsTypeProto> encodeMetricsType = metricsTransfer.encodeMetricsType(metricsType);
		
		Map<String, List<MetricsMapProto>> metricsByNode = 
				this.stubCenter.getAllNodeMetrics(applicationId, encodeMetricsType);
		
		List<NodeBean> nodes = new ArrayList<NodeBean>(metricsByNode.size());
		
		
		for(Entry<String, List<MetricsMapProto>> nodeMetrics: metricsByNode.entrySet()){
			NodeBean node = new NodeBean();
			node.setHost(nodeMetrics.getKey());
			
			for(MetricsMapProto metricProto: nodeMetrics.getValue()){
				MetricBean metricBean = new MetricBean();
				metricBean.setName(metricProto.getMetricsName());
				
				for(TupleProto tupleProto: metricProto.getMetricsValuesList()){
					TimeValuePairBean pair = new TimeValuePairBean();
					pair.setTime(tupleProto.getTime());
					pair.setValue(tupleProto.getValue());
					metricBean.addPair(pair);
				}
				node.addMetrics(metricBean);
			}
			nodes.add(node);
		}
		
		return nodes;
	}

	@Override
	public NodeBean getNodeMetrics(String node, 
			String applicationId, List<String> metricsType) throws Exception {
		MetricsDataTransfer metricsTransfer = new MetricsDataTransfer();
		List<MetricsTypeProto> encodeMetricsType = metricsTransfer.encodeMetricsType(metricsType);
		
		List<MetricsMapProto> nodeMetrics = this.stubCenter.getNodeMetrics(node, applicationId, encodeMetricsType);
		
		NodeBean nodeBean = new NodeBean();
		nodeBean.setHost(node);
		for(MetricsMapProto metricProto: nodeMetrics){
			MetricBean metricBean = new MetricBean();
			metricBean.setName(metricProto.getMetricsName());
			
			for(TupleProto tupleProto: metricProto.getMetricsValuesList()){
				TimeValuePairBean pair = new TimeValuePairBean();
				pair.setTime(tupleProto.getTime());
				pair.setValue(tupleProto.getValue());
				metricBean.addPair(pair);
			}
			nodeBean.addMetrics(metricBean);
		}
		
		return nodeBean;
	}

}
