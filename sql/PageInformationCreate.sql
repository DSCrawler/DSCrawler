narf //purposeful syntax error
use dealersocket;

drop table if exists WebProviderMatches;
drop table if exists SchedulerMatches;
drop table if exists GeneralMatches;
drop table if exists UrlExtractions;
drop table if exists StringExtractions;
drop table if exists PageInformation;
drop table if exists SiteInformation;

create table SiteInformation (
	SITE_ID bigint AUTO_INCREMENT primary key,
	SITE_NAME varchar(255),
	SITE_URL varchar(300) not null,
	NIADA_ID varchar(15),
	CAPDB_ID varchar(15),
	GIVEN_ADDRESS varchar(100),
	GIVEN_URL varchar(300),
	INTERMEDIATE_URL varchar (300),
	CRAWL_FROM_GIVEN_URL boolean,
	CRAWL_DATE datetime
);
create table PageInformation (
	PAGE_ID bigint auto_increment primary key,
	PATH varchar(300) not null,
    SITE_ID bigint not null,
    RAW_TEXT mediumtext,
	foreign key(SITE_ID) references SiteInformation(SITE_ID)
);

create table WebProviderMatches (

	PAGE_ID bigint not null,
	DEALER_COM int not null default 0,
    FRESH_INPUT int not null default 0,
    JAZELAUTOS int not null default 0,
    FORD_DIRECT int not null default 0,
    CLICK_MOTIVE int not null default 0,
    VINSOLUTIONS int not null default 0,
    COBALT int not null default 0,
    COBALT_NITRA int not null default 0,
    COBALT_GROUP int not null default 0,
    DEALER_ON int not null default 0,
    DEALER_ON_SECONDARY int not null default 0,
    AUTO_ONE int not null default 0,
    DEALER_APEX int not null default 0,
    DEALER_CAR_SEARCH int not null default 0,
    DEALERFIRE int not null default 0,
    DEALERINSPIRE int not null default 0,
    DEALER_LAB int not null default 0,
    SOKAL_MEDIA_GROUP int not null default 0,
    MOTION_FUZE int not null default 0,
    DEALER_TRACK int not null default 0,
    DEALER_EPROCESS int not null default 0,
    DEALER_EPROCESS_PRIMARY int not null default 0,
    DEALER_ZOOM int not null default 0,
    DEALER_ZOOM_SECONDARY int not null default 0,
    DOMINION int not null default 0,
    DRIVE_WEBSITE int not null default 0,
    FUSION_ZONE int not null default 0,
    INTERACTIVE_360 int not null default 0,
    LIQUID_MOTORS int not null default 0,
    NAKED_LIME int not null default 0,
    NAKED_LIME_SECONDARY int not null default 0,
    POTRATZ int not null default 0,
    POTRATZ_SECONDARY int not null default 0,
    CARS_FOR_SALE int not null default 0,
    CARBASE int not null default 0,
    EBIZ_AUTOS int not null default 0,
    ELEAD_DIGITAL_CRM int not null default 0,
    SMART_DEALER_SITES int not null default 0,
    DIGIGO int not null default 0,
	foreign key(PAGE_ID) references PageInformation(Page_ID)
    );
    
create table SchedulerMatches (
	PAGE_ID bigint not null,
	COBALT_SCHEDULER int not null default 0,
    XTIME_SCHEDULER int not null default 0,
    OTHER_XTIME int not null default 0,
    AUTO_APPOINTMENTS_SCHEDULER int not null default 0,
    TIME_HIGHWAY_SCHEDULER int not null default 0,
    VIN_SOLUTION_SCHEDULER int not null default 0,
    MY_VEHICLE_SITE_SCHEDULER int not null default 0,
    TOTAL_CUSTOMER_CONNECT_SCHEDULER int not null default 0,
    DEALER_CONNECTION_SCHEDULER int not null default 0,
    ADP_SCHEDULER int not null default 0,
    ADP_SCHEDULER_BACKUP int not null default 0,
    ADP_OLD_SCHEDULER int not null default 0,
    ADP_OLD_SCHEDULER_ALTERNATIVE int not null default 0,
    SHOPWATCH_SCHEDULER int not null default 0,
    ACUITY_SCHEDULER int not null default 0,
    CIMA_SYSTEMS int not null default 0,
    CIMA_SYSTEMS_SECONDARY int not null default 0,
    UDC_REVOLUTION int not null default 0,
    DEALER_SOCKET int not null default 0,
    DEALER_FX_SCHEDULER int not null default 0,
    SERVICE_BOOK_PRO_SCHEDULER int not null default 0,
    AD_WORKZ_SCHEDULER int not null default 0,
    CAR_RESEARCH_SCHEDULER int not null default 0,
    DRIVERSIDE_SCHEDULER int not null default 0,
    DEALERMINE_SCHEDULER int not null default 0,
    PBS_SYSTEMS_SCHEDULER int not null default 0,
    LEAD_RESULT_SCHEDULER int not null default 0,
    SCHEDULE_WEB_PRO_SCHEDULER int not null default 0,
    REYNOLDS_SCHEDULER int not null default 0,
    foreign key(PAGE_ID) references PageInformation(Page_ID)
);

create table GeneralMatches (
	PAGE_ID bigint not null,
	DEALER_COM_POWERED_BY int not null default 0,
    USES_CLIENT_CONNEXION int not null default 0,
    VIN_LENS int not null default 0,
    SKYSA int not null default 0,
    DEALER_COM_VERSION_9 int not null default 0,
    DEALER_COM_VERSION_8 int not null default 0,
    DEMDEX int not null default 0,
    CONTACTATONCE int not null default 0,
    DEALERVIDEOS int not null default 0,
    OUTSELLCAMPAIGNSTORE int not null default 0,
    CALL_MEASUREMENT int not null default 0,
    SHOWROOM_LOGIC int not null default 0,
    COLLSERVE int not null default 0,
    SPEEDSHIFTMEDIA int not null default 0,
    AKAMAI int not null default 0,
    CUTECHAT int not null default 0,
    CARCODE_SMS int not null default 0,
    YOAST int not null default 0,
    LOT_LINX int not null default 0,
    INSPECTLET int not null default 0,
    MOTOFUZE int not null default 0,
    CLICKY int not null default 0,
    GUBA_GOO_TRACKING int not null default 0,
    ADD_THIS int not null default 0,
    E_CARLIST int not null default 0,
    WUFOO_FORMS int not null default 0,
    DEALER_EPROCESS_CHAT int not null default 0,
    APPNEXUS int not null default 0,
    MY_VEHICLE_SITE int not null default 0,
    PURE_CARS int not null default 0,
    FETCHBACK int not null default 0,
    FORCETRAC int not null default 0,
    CLOUDFLARE int not null default 0,
    SOCIAL_CRM_360 int not null default 0,
    BLACKBOOK_INFORMATION int not null default 0,
    AD_ROLL int not null default 0,
    GHOSTERY int not null default 0,
    GHOSTERY_SECOND int not null default 0,
    NAKED_LIME_IMAGES int not null default 0,
    ACTIVE_ENGAGE int not null default 0,
    SHARP_SPRING int not null default 0,
    ADOBE_TAG_MANAGER int not null default 0,
    DEALER_CENTRIC int not null default 0,
    AUTO_TRADER_PLUGIN int not null default 0,
    BOLD_CHAT int not null default 0,
    VOICESTAR int not null default 0,
    DEALERSHIP_INTEGRATED_DATA_SOLUTIONS int not null default 0,
    HAS_GOOGLE_PLUS int not null default 0,
    PLUS_ONE_BUTTON int not null default 0,
    USES_GOOGLE_ANALYTICS int not null default 0,
    USES_GOOGLE_AD_SERVICES int not null default 0,
    GOOGLE_TAG_MANAGER int not null default 0,
    JQUERY int not null default 0,
    GOOGLE_MAPS int not null default 0,
    APPLE_APP int not null default 0,
    ANDROID_APP int not null default 0,
    POSTS_INSTAGRAM_TO_SITE int not null default 0,
    GOOGLE_TRANSLATE int not null default 0,
    YOUTUBE_EMBEDDED int not null default 0,
    foreign key(PAGE_ID) references PageInformation(Page_ID)
);

create table UrlExtractions (
	PAGE_ID bigint not null,
	FACEBOOK text ,
  	GOOGLE_PLUS text ,
  	TWITTER text ,
  	YOUTUBE text ,
  	FLICKER text ,
  	INSTAGRAM text ,
  	YELP text ,
  	LINKED_IN text ,
  	PINTEREST text ,
  	FOURSQUARE text,
    foreign key(PAGE_ID) references PageInformation(Page_ID)
);

create table StringExtractions (
	PAGE_ID bigint not null,
    EMAIL_ADDRESS text,
    GOOGLE_ANALYTICS_CODE text,
    foreign key(PAGE_ID) references PageInformation(Page_ID)
)