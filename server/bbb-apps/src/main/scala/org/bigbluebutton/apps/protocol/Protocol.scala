package org.bigbluebutton.apps.protocol

import spray.json.JsValue
import org.bigbluebutton.apps.models.UsersApp.User
import spray.json.DefaultJsonProtocol
import spray.httpx.SprayJsonSupport
import org.bigbluebutton.apps.models.UsersApp.JoinedUser

case class Header(event: HeaderEvent, meeting: HeaderMeeting, 
                  response: Option[Response] = None)
                  
case class HeaderEvent(name: String, timestamp: Long, 
                       source: String, reply: Option[ReplyHeader])
                       
case class ReplyHeader(to: String, correlationId: String)                       
case class HeaderMeeting(name: String, externalId: String, sessionId: Option[String] = None)
case class HeaderAndPayload(header: Header, payload: JsValue)
case class ReplyStatus(status: String, message: String, error: Option[Int])

case class StatusCode(code: Int, message: String)
case class ErrorCode(code: Int, message: String, details: Option[String] = None)
case class Response(status: StatusCode, errors: Option[Seq[ErrorCode]] = None)

case class ResponsePayload(response: Response)
case class JsonResponse(header: Header, payload: Option[ResponsePayload] = None)

case class MessageProcessException(message: String) extends Exception(message)

object InMessageNameContants {
  val CreateMeetingRequestMessage = "CreateMeetingRequest"
  val RegisterUserRequestMessage = "RegisterUserRequest"
}

object HeaderAndPayloadJsonSupport extends DefaultJsonProtocol with SprayJsonSupport {  
  implicit val statusCodeFormat = jsonFormat2(StatusCode)  
  implicit val errorCodeFormat = jsonFormat3(ErrorCode)
  implicit val responseFormat = jsonFormat2(Response)
  implicit val replyHeaderFormat = jsonFormat2(ReplyHeader)
  implicit val headerEventFormat = jsonFormat4(HeaderEvent)
  implicit val headerMeetingFormat = jsonFormat3(HeaderMeeting)
  implicit val headerFormat = jsonFormat3(Header)  
  implicit val headerAndPayloadFormat = jsonFormat2(HeaderAndPayload)
  implicit val responsePayloadFormat = jsonFormat1(ResponsePayload)
  implicit val jsonResponseFormat = jsonFormat2(JsonResponse)
}

object StatusCodes extends Enumeration {
  type StatusCodeType = Value
  
  val OK = Value(200, "OK")
  val NOT_MODIFIED = Value(304, "Not Modified")
  val BAD_REQUEST =  Value(400, "Bad Request")
  val UNAUTHORIZED = Value(401, "Unauthorized")
  val FORBIDDEN = Value(403, "Forbidden")
  val NOT_FOUND = Value(404, "Not Found")
  val NOT_ACCEPTABLE = Value(406, "Not Acceptable")
  val INTERNAL_SERVER_ERROR = Value(500, "Internal Server Error")
  val BAD_GATEWAY = Value(502, "Bad Gateway")
  val SERVICE_UNAVAILABLE = Value(503, "Service Unavailable")
}

object StatusCodeBuilder {
  def buildStatus(codeType: StatusCodes.StatusCodeType):StatusCode = {
    StatusCode(codeType.id, codeType.toString())
  }
}

object ErrorCodes extends Enumeration {
  type ErrorCodeType = Value
  
  val INVALID_TOKEN = Value(1, "Invalid or expired token")
  val INVALID_MESSAGE = Value(2, "Invalid message")
  
}

object ErrorCodeBuilder {
  def buildError(codeType: ErrorCodes.ErrorCodeType, details: String):ErrorCode = {
    ErrorCode(codeType.id, codeType.toString(), Some(details))
  }
}

object HeaderBuilder {
  val eventSource = "bbb-apps"
    
  def buildResponseHeader(name: String, header: HeaderEvent): HeaderEvent = {
    header.copy(name = name, 
                timestamp = System.currentTimeMillis(), 
                source = eventSource)
  }
}