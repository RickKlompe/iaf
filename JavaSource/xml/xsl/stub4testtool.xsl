<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">
	<xsl:output method="xml" indent="yes" />
	<!--
		This XSLT adjusts the IBIS configuration as follows:
		- disable all receiver elements, except those with childs JdbcQueryListener, DirectoryListener and JavaListener
		- add a default receiver (name="testtool-[adapter name]") with child JavaListener (serviceName="testtool-[adapter name]") to each adapter
		- disable all listener elements with parent pipe		
		- stub alle sender elements with parent pipe by an IbisJavaSender (serviceName="testtool-[pipe name]"), except the DirectQuerySender, FixedQuerySender, DelaySender, EchoSender, IbisLocalSender, LogSender, ParallelSenders, SenderSeries, SenderWrapper and XsltSender
		- disable all elements sapSystems
		- disable all elements jmsRealm with attribute queueConnectionFactoryName (if combined with the attribute datasourceName a new jmsRealm for this datasourceName is created)
		- add the attribute returnFixedDate with value true to alle pipe elements PutSystemDateInSession
	-->
	<xsl:template match="/">
		<xsl:apply-templates select="*|@*|comment()|processing-instruction()" />
	</xsl:template>
	<xsl:template match="*|@*|comment()|processing-instruction()">
		<xsl:choose>
			<xsl:when test="name()='receiver'">
				<xsl:choose>
					<xsl:when test="listener[@className='nl.nn.adapterframework.jdbc.JdbcQueryListener']">
						<xsl:call-template name="copy" />
					</xsl:when>
					<xsl:when test="listener[@className='nl.nn.adapterframework.receivers.DirectoryListener']">
						<xsl:call-template name="copy" />
					</xsl:when>
					<xsl:when test="listener[@className='nl.nn.adapterframework.receivers.JavaListener']">
						<xsl:call-template name="copy" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="disable" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="name()='pipeline'">
				<xsl:element name="receiver">
					<xsl:attribute name="className">nl.nn.adapterframework.receivers.GenericReceiver</xsl:attribute>
					<xsl:attribute name="name">
						<xsl:value-of select="concat('testtool-',parent::*[name()='adapter']/@name)" />
					</xsl:attribute>
					<xsl:element name="listener">
						<xsl:attribute name="className">nl.nn.adapterframework.receivers.JavaListener</xsl:attribute>
						<xsl:attribute name="serviceName">
							<xsl:value-of select="concat('testtool-',parent::*[name()='adapter']/@name)" />
						</xsl:attribute>
					</xsl:element>
				</xsl:element>
				<xsl:call-template name="copy" />
			</xsl:when>
			<xsl:when test="name()='listener'">
				<xsl:choose>
					<xsl:when test="parent::*[name()='pipe']">
						<xsl:call-template name="disable" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="copy" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="name()='sender'">
				<xsl:choose>
					<xsl:when test="parent::*[name()='pipe']">
						<xsl:variable name="pipeName" select="parent::*[name()='pipe']/@name" />
						<xsl:choose>
							<xsl:when test="@className='nl.nn.adapterframework.jdbc.DirectQuerySender'">
								<xsl:call-template name="copy" />
							</xsl:when>
							<xsl:when test="@className='nl.nn.adapterframework.jdbc.FixedQuerySender'">
								<xsl:call-template name="copy" />
							</xsl:when>
							<xsl:when test="@className='nl.nn.adapterframework.senders.DelaySender'">
								<xsl:call-template name="copy" />
							</xsl:when>
							<xsl:when test="@className='nl.nn.adapterframework.senders.EchoSender'">
								<xsl:call-template name="copy" />
							</xsl:when>
							<xsl:when test="@className='nl.nn.adapterframework.senders.IbisLocalSender'">
								<xsl:call-template name="copy" />
							</xsl:when>
							<xsl:when test="@className='nl.nn.adapterframework.senders.LogSender'">
								<xsl:call-template name="copy" />
							</xsl:when>
							<xsl:when test="@className='nl.nn.adapterframework.senders.ParallelSenders'">
								<xsl:call-template name="copy" />
							</xsl:when>
							<xsl:when test="@className='nl.nn.adapterframework.senders.SenderSeries'">
								<xsl:call-template name="copy" />
							</xsl:when>
							<xsl:when test="@className='nl.nn.adapterframework.senders.SenderWrapper'">
								<xsl:call-template name="copy" />
							</xsl:when>
							<xsl:when test="@className='nl.nn.adapterframework.senders.XsltSender'">
								<xsl:call-template name="copy" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:element name="sender">
									<xsl:if test="string-length(@name)&gt;0">
										<xsl:attribute name="name">
											<xsl:value-of select="@name" />
										</xsl:attribute>
									</xsl:if>
									<xsl:attribute name="className">nl.nn.adapterframework.senders.IbisJavaSender</xsl:attribute>
									<xsl:attribute name="serviceName">
										<xsl:value-of select="concat('testtool-',$pipeName)" />
									</xsl:attribute>
								</xsl:element>
								<xsl:call-template name="disable" />
							</xsl:otherwise>
						</xsl:choose>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="copy" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:when test="name()='sapSystems'">
				<xsl:call-template name="disable" />
			</xsl:when>
			<xsl:when test="name()='jmsRealm' and @queueConnectionFactoryName">
				<xsl:call-template name="disable" />
				<xsl:if test="@datasourceName">
					<xsl:element name="jmsRealm">
						<xsl:attribute name="realmName">
							<xsl:value-of select="@realmName"/>
						</xsl:attribute>
						<xsl:attribute name="datasourceName">
							<xsl:value-of select="@datasourceName"/>
						</xsl:attribute>
					</xsl:element>
				</xsl:if>
			</xsl:when>
			<xsl:when test="name()='pipe' and @className='nl.nn.adapterframework.pipes.PutSystemDateInSession'">
				<xsl:element name="pipe">
					<xsl:apply-templates select="@*" />
					<xsl:attribute name="returnFixedDate">true</xsl:attribute>
					<xsl:apply-templates select="*|comment()|processing-instruction()|text()" />
				</xsl:element>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="copy" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="disable">
		<xsl:text disable-output-escaping="yes">&lt;!--</xsl:text>
		<xsl:copy>
			<xsl:apply-templates select="*|@*|processing-instruction()|text()" />
		</xsl:copy>
		<xsl:text disable-output-escaping="yes">--&gt;</xsl:text>
	</xsl:template>
	<xsl:template name="copy">
		<xsl:copy>
			<xsl:apply-templates select="*|@*|comment()|processing-instruction()|text()" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>