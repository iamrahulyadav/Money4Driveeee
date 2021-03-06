package com.hvantage2.money4driveeee.util;

public class AppConstants {
    public static final int IMAGE_COMPRESS_QUALITY = 30;

    public interface BASEURL {
        //String URL = "http://www.truckslogistics.com/Projects-Work/M4D/MOBILE_APP/";
        //String URL = "http://take007.co.in/Projects-Work/M4D/MOBILE_APP/";
        String URL = "http://developerhvantage.co.in/M4D/MOBILE_APP/";
    }

    public interface ENDPOINT {
        String LOGIN = "Register_Log_Api.php";
        String DASHBOARD = "Dashboard_API.php";
        String PROJECTAPI = "Project_API.php";
        String PROJECT_ACTIVITY_API = "Project_Activity_API.php";
        String PROJECT_TRANSIT_API = "Project_Transit_API.php";
        String PROJECT_SHOP_DETAIL_API = "Project_Shop_Detail_API.php";
        String PROJECT_TRANSIT_DETAIL_API = "Project_Transit_Detail_Api.php";
        String REGISTER_LOG_API = "Register_Log_Api.php";
        String PROJECT_PRINT_DETAIL_API = "Project_Print_Detail_Api.php";
        String PROJECT_HOARDINGS_API = "Project_Hoardings_Api.php";
        String PROJECT_WALL_API = "Project_Wall_Api.php";
        String PROJECT_EMEDIA_API = "Project_EMedia_Api.php";
        String USER_CHAT_API = "User_Chat_Api.php";
        String NEW_PROJECT_API = "New_Project_Api.php";
    }

    public interface FEILDEXECUTATIVE {
        String USER_LOGIN = "User_Login";
        String FORGOT_PASSWORD = "Forgot_Password";
        String UPDATE_PROFILE = "Update_profile";
        String USERLOGOUT = "UserLogout";
        String USERDASHBOARD = "userDashboard";
        String PENDINGPROJECTS = "PendingProjects";
        String COMPLETEPROJECTS = "CompleteProjects";
        String PROJECTSDETAILS = "ProjectsDetails";
        String PROJECTSSHOPDETAILS = "ProjectsShopDetails";
        String SINGLE_TASK_COMP = "SingleActivityTaskComplete";
        String SAVESHOPDETAIL = "saveShopDetail";
        String SHOPACTIVITYDETAIL = "shopActivityDetail";
        String PROJECTACTIVITYLIST = "projectActivityList";
        String PROJECTACTIVITYIMAGEUPLOAD = "projectActivityImageUpload";
        String DELETEACTIVITYDETAIL = "DeleteActivityDetail";
        String EDITACTIVITYDETAIL = "EditActivityDetail";
        String GETTRANSITLIST = "getTransitList";
        String GETTRANSITDETAIL = "getTransitDetail";
        String UPDATETRANSITDETAILS = "updateTransitDetails";
        String TRANSIT_SINGLE_ACTIVITY_TASKCOMPLETE = "TransitSingleActivityTaskComplete";
        String TRANSITACTIVITYDETAIL = "transitActivityDetail";
        String TRANSITPROJECTACTIIMGUPLOAD = "TransitProjectActiImgUpload";
        String TRANSITEDITACTIVITYDETAIL = "TransitEditActivityDetail";
        String DELETETRANSITACTDET = "DeleteTransitActDet";
        String PROJECTSHOPLISTS = "ProjectShopLists";
        String PROJECTACTIVIYLISTS = "ProjectActiviyLists";
        String CHECKSHOPNUMBER = "CheckShopNumber";
        String ADDSHOPDETAIL = "AddShopDetail";
        String CHECKREGISTERATIONNUMBER = "CheckRegisterationNumber";
        String ADDTRANSITDETAIL = "AddTransitDetail";
        String GETSHOPMEDIATYPE = "getShopMediaType";
        String GETSHOPMEDIAOPTION = "getShopMediaOption";
        String PROJECTTRANSITACTIVITYLIST = "projectTransitActivityList";
        String UPDATE_FCM_TOKEN = "update_fcm_token";
        String GETPROJECTPRINTMEDIALIST = "GetProjectPrintMediaList";
        String PROJECTSPRINTMEDIADETAILS = "ProjectsPrintMediaDetails";
        String SAVEMEDIADETAIL = "saveMediaDetail";
        String PRINT_SINGLE_ACTIVITY_TASKCOMPLETE = "SinglePrintTaskComplete";
        String PRINTACTIVITYDETAIL = "PrintActivityDetail";
        String PROJECTPRINTIMAGEUPLOAD = "projectPrintImageUpload";
        String EDITPRINTDETAIL = "EditPrintDetail";
        String DELETEPRINTDETAIL = "DeletePrintDetail";
        String PROJECTHOARDINGLIST = "ProjectHoardingList";
        String ADDHOARDINGDETAIL = "AddHoardingDetail";
        String SINGLEHOARDINGTASKCOMPLETE = "SingleHoardingTaskComplete";
        String HOARDINGACTIVITYDETAIL = "HoardingActivityDetail";
        String PROJECTSHOARDINGDETAILS = "ProjectsHoardingDetails";
        String SAVEHOARDINGDETAIL = "saveHoardingDetail";
        String PROJECTHOARDINGIMAGEUPLOAD = "projectHoardingImageUpload";
        String EDITHOARDINGDETAIL = "EditHoardingDetail";
        String DELETEHOARDINGIDETAIL = "DeleteHoardingDetail";
        String PROJECTWALLLIST = "ProjectWallList";
        String ADDWALLDETAIL = "AddWallDetail";
        String PROJECTSWALLDETAILS = "ProjectsWallDetails";
        String SAVEWALLDETAIL = "saveWallDetail";
        String EDITWALLDETAIL = "EditWallDetail";
        String PROJECTWALLMAGEUPLOAD = "projectWallmageUpload";
        String WALLACTIVITYDETAIL = "WallActivityDetail";
        String SINGLEWALLTASKCOMPLETE = "SingleWallTaskComplete";
        String DELETEWALLDETAIL = "DeleteWallDetail";
        String PROJECTEMEDIALIST = "ProjectEMediaList";
        String PROJECTSEMEDIADETAILS = "ProjectsEMediaDetails";
        String SAVEEMEDIADETAIL = "saveEMediaDetail";
        String SINGLEEMEDIATASKCOMPLETE = "SingleEMediaTaskComplete";
        String EDITEMEDIADETAIL = "EditEMediaDetail";
        String EMEDIAACTIVITYDETAIL = "EMediaActivityDetail";
        String PROJECTEMEDIAIMAGEUPLOAD = "projectEMediaImageUpload";
        String DELETEEMEDIADETAIL = "DeleteEMediaDetail";
        String USERALLPROJECTLISTDATA = "UserAllProjectListData";
        String GETPROJECTMSGS = "getProjectMsgs";
        String SENDMSGTOPROJECT = "sendMsgToProject";
        String ADD_VEHICLE_WITH_VERIFY = "add_vehicle_with_verify";
        String VEHICLE_VERIFY_OTP = "vehicle_verify_otp";
        String GETPROJECTSTATES = "getProjectStates";
        String GETPROJECTCITIES = "getProjectCities";
        String GETCHECKREGISRATIONNUMBER = "getCheckRegisrationNumber";
        String TRANSIT_REPASTE = "transit_repaste";
    }

    public class KEYS {
        public static final String ADDRESS = "address";
        public static final String CITY = "city";
        public static final String STATE = "state";
        public static final String VEHICLE_REGIS_NUMBER = "vehicle_regis_number";
        public static final String VEHICLE_MODEL = "vehicle_model";
        public static final String DRIVER_CONTACT_NO = "driver_contact_no";
        public static final String DRIVER_NAME = "driver_name";
        public static final String VEHICLE_ID = "vehicle_id";
        public static final String TRANSIT_ID = "transit_id";
        public static final String PROJECT_ID = "project_id";
        public static final String USER_ID = "user_id";
        public static final String METHOD = "method";
        public static final String LOGIN_TYPE_ID = "login_type_id";
        public static final String BRANDING_ID = "branding_id";
        public static final String FCM_ID = "FCM_ID";
    }

    public class MEDIATYPE {
        public static final String PRINT_MEDIA = "1";
        public static final String SHOP_MEDIA = "2";
        public static final String TRANSIT_MEDIA = "3";
        public static final String ELECTRONIC_MEDIA = "4";
        public static final String WALL_PAINTING = "5";
        public static final String HOARDINGS = "6";
    }

    public class PROJECT_TYPE {
        public static final String COMPLETED = "completed";
        public static final String PENDING = "pending";
    }

    public class PROJECT_TYPE_IDS {
        public static final String PENDING_ID = "1";
        public static final String COMPLETED_ID = "2";
    }

    public class NOTIFICATION_KEY {
        public static final String NEW_PROJECT_ASSIGNED = "new_project_assigned";
        public static final String PROJECT_STATUS_CHANGED = "project_status_changed";
        public static final String NEW_MSG_ARRIVED = "new_msg_arrived";
        public static final String MSG_DATA = "msg_data";
    }
}
