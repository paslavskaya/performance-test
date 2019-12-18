import org.boon.Boon
import groovy.json.JsonSlurper

def jsonFile = new File("${parametersFilePath}")
def jsonConfig = ''

if (jsonFile.exists()){
    jsonConfig = new JsonSlurper().parseText(jsonFile.text)
}
else
    jsonConfig = new JsonSlurper().parseText'''{}'''

if (jsonConfig.websiteUrl == null)
    jsonConfig.websiteUrl = ''

if (jsonConfig.language == null)
    jsonConfig.language = '/en-us/'

if (jsonConfig.ntPopupUserName == null)
    jsonConfig.ntPopupUserName = ''

if (jsonConfig.ntPopupPassword == null)
    jsonConfig.ntPopupPassword = ''

if (jsonConfig.login_usersCount == null)
    jsonConfig.login_usersCount = '1'

if (jsonConfig.login_users == null)
    jsonConfig.login_users = 'clair@sana-commerce.com;demosana'

if (jsonConfig.rampUpTime == null)
    jsonConfig.rampUpTime = '1'

if (jsonConfig.plp_usersCount == null)
    jsonConfig.plp_usersCount = '1'

if (jsonConfig.plp_navigationItem == null)
    jsonConfig.plp_navigationItem = 'Catalog'

if (jsonConfig.plp_loopCount == null)
    jsonConfig.plp_loopCount = '1'

if (jsonConfig.search_usersCount == null)
    jsonConfig.search_usersCount = '1'

if (jsonConfig.search_loopCount == null)
    jsonConfig.search_loopCount = '1'       

if (jsonConfig.searchTerms == null)
    jsonConfig.searchTerms = '1'

if (jsonConfig.plpUrl == null)
   jsonConfig.plpUrl = ''

if (jsonConfig.pdp_usersCount == null)
    jsonConfig.pdp_usersCount = '1'

if (jsonConfig.pdp_loopCount == null)
    jsonConfig.pdp_loopCount = '1'

if (jsonConfig.basket_usersCount == null)
    jsonConfig.basket_usersCount = '1'

if (jsonConfig.basket_loopCount == null)
    jsonConfig.basket_loopCount = '1'

if (jsonConfig.order_usersCount == null)
    jsonConfig.order_usersCount = '1'

def jsonEditorOptions = Boon.fromJson(/{
  disable_edit_json: true,
  disable_properties: true,
  no_additional_properties: true,
  disable_collapse: true,
  disable_array_add: true,
  disable_array_delete: true,
  disable_array_reorder: true,
  theme: "bootstrap3",
  iconlib:"fontawesome4",
  schema: {
    "type": "object",
    "title": "Performance test configuration",
    "properties": {
      "websiteUrl": {
        "type": "string",
        "format": "url",
        "title": "Website URL",
        "description": "example: allison-eu.sana-commerce.com",
        "propertyOrder": 0,
        "default": "${jsonConfig.websiteUrl}"
      },
      "language": {
        "type": "string",
        "title": "Language",
        "description": "",
        "propertyOrder": 1,
        "default": "${jsonConfig.language}"
      },
      "ntPopupUserName": {
        "type": "string",
        "title": "NTPopup username",
        "description": "",
        "propertyOrder": 2,
        "default": "${jsonConfig.ntPopupUserName}"
      },
      "ntPopupPassword": {
        "type": "string",
        "title": "NTPopup password",
        "description": "",
        "propertyOrder": 3,
        "default": "${jsonConfig.ntPopupPassword}"
      },
      "rampUpTime": {
        "type": "string",
        "title": "Ramp up time",
        "description": "Indicates how fast you want the test to ramp-up. This is the elapsed time in minutes from test start until all users are running.",
        "propertyOrder": 4,
        "default": "${jsonConfig.rampUpTime}"
      },
      "scenarios": {
        "type": "object",
        "title": "Select performance scenarios to run",
        "format": "tabs",
        "propertyOrder": 5,
        "properties": {
          "a_login": {
            "type": "object",
            "title": "Logged in customers or anonymous?",
            "propertyOrder": 1,
            "oneOf": [
              {
                "title": "Logged in",
                "type": "object",
                "description": "Make sure you filled in 'Shop accounts to use' field below and imported these shop accounts in Sana admin",
                "properties": {
                  "enabled": {
                  "type": "string",
                  "format": "hidden",
                  "title": "",
                  "propertyOrder": 0,
                  "default": "true"
                  },
                  "usersCount": {
                    "type": "string",
                    "title": "Users count",
                    "description": "Number of concurrent users logging to webshop",
                    "propertyOrder": 1,
                    "default": "${jsonConfig.login_usersCount}"
                  },
                  "users": {
                    "type": "string",
                    "format": "textarea",
                    "title": "Shop accounts to use",
                    "description": "Put the list of shop accounts for which login should be executed.
                    Format is the following: shopAccountEmail;password.
                    Note, each user should be in separate line",
                    "propertyOrder": 4,
                    "default": "${jsonConfig.login_users}"
                  }
                }
              },
              {
                "title": "Anonymous",
                "type": "object", 
                "description": "Make sure your shop is Public."
              }
            ]
          },
          "b_plp": {
            "type": "object",
            "title": "Navigate to product list pages from main navigation",
            "propertyOrder": 2,
            "oneOf": [
              {
              "title": "Run scenario",
              "type": "object",
              "properties": {
                "enabled": {
                  "type": "string",
                  "format": "hidden",
                  "title": "",
                  "propertyOrder": 0,
                  "default": "true"
                  },
                "usersCount": {
                  "type": "string",
                  "title": "Users count",
                  "description": "Number of concurrent users navigating to PLP pages",
                  "propertyOrder": 1,
                  "default": "${jsonConfig.plp_usersCount}"
                  },
                "navigationItem": {
                  "type": "string",
                  "title": "Name of catalog navigation item to explore",
                  "description": "Specify name of main navigation item, which contains product list pages links",
                  "propertyOrder": 2,
                  "default": "${jsonConfig.plp_navigationItem}"
                  },
                "loopCount": {
                  "type": "string",
                  "title": "Number of product list pages each user should open",
                  "description": "Specify MAX number of product list pages each user should open. Product list link will be randomly selected from main navigation each time.",
                  "propertyOrder": 3,
                  "default": "${jsonConfig.plp_loopCount}"
                  }
                }
              },
              {
              "title": "Do not run",
              "type": "object"
              }
            ]
          },
          "c_search": {
            "type": "object",
            "title": "Perform search",
            "propertyOrder": 3,
            "oneOf": [
              {
                "title": "Run scenario",
                "type": "object",
                "properties": {
                  "enabled": {
                    "type": "string",
                    "format": "hidden",
                    "title": "",
                    "propertyOrder": 0,
                    "default": "true"
                  },
                  "usersCount": {
                    "type": "string",
                    "title": "Users count",
                    "description": "Number of concurrent users performing search",
                    "propertyOrder": 1,
                    "default": "${jsonConfig.search_usersCount}"
                  },
                   "loopCount": {
                    "type": "string",
                    "title": "How much times each user should perform search?",
                    "description": "Specify MAX number of times user should perform the search",
                    "propertyOrder": 2,
                    "default": "${jsonConfig.search_loopCount}"
                  },
                  "searchTerms": {
                    "type": "string",
                    "title": "Search terms",
                    "format": "textarea",
                    "description": "Specify the most popular search terms, users will search for. Put each search term in a separate line",
                    "propertyOrder": 3,
                    "default": "${jsonConfig.searchTerms}"
                  }
                }
              },
              {
                "title": "Do not run",
                "type": "object"
              }
            ]
          }, 
          "d_pdp": {
            "type": "object",
            "title": "Navigate to product detail pages",
            "propertyOrder": 4,
            "oneOf": [
              {
                "title": "Run scenario",
                "type": "object",
                "properties": {
                  "enabled": {
                    "type": "string",
                    "format": "hidden",
                    "title": "",
                    "propertyOrder": 0,
                    "default": "true"
                    },
                  "plpUrl": {
                    "type": "string",
                    "title": "Product list url (relative, without domain and language)",
                    "description": "Specify relative url of product list with products for navigation to details page",
                    "propertyOrder": 1,
                    "default": "${jsonConfig.plpUrl}"
                    },
                  "usersCount": {
                    "type": "string",
                    "title": "Users count",
                    "description": "Number of concurrent users navigating to PDP pages",
                    "propertyOrder": 2,
                    "default": "${jsonConfig.pdp_usersCount}"
                    },
                  "loopCount": {
                    "type": "string",
                    "title": "Number of product pages each user should open",
                    "description": "Specify MAX number of product pages each user should open. Product details link will be randomly selected",
                    "propertyOrder": 3,
                    "default": "${jsonConfig.pdp_loopCount}"
                    }
                  }
              },
              {
                "title": "Do not run",
                "type": "object"
              }
            ]
          },
          "e_addToBasket":{
            "type": "object",
            "title": "Add product to basket",            
            "propertyOrder": 5,
            "oneOf": [
              {
                "title": "Run scenario",
                "type": "object",
                "properties": {
                  "enabled": {
                    "type": "string",
                    "format": "hidden",
                    "title": "",
                    "propertyOrder": 0,
                    "default": "true"
                  },
                  "usersCount": {
                    "type": "string",
                    "title": "Users count",
                    "description": "Number of concurrent users adding product to basket",
                    "propertyOrder": 1,
                    "default": "${jsonConfig.basket_usersCount}"
                  },
                  "loopCount": {
                    "type": "string",
                    "title": "Max number of products each user should add to basket",
                    "description": "Specify MAX number of products each user adds to basket",
                    "propertyOrder": 2,
                    "default": "${jsonConfig.basket_loopCount}"
                  },
                 "addToBasketFrom": {
                   "type": "object",
                   "title": "Where do you want to add product from?",
                   "propertyOrder": 3,
                   "oneOf": [
                    {
                      "title": "Add product to basket from Search results page",
                      "type": "object",
                      "description": "NOTE! 'Perform search' scenario should be enabled",
                       "properties": {
                          "search": {
                            "type": "string",
                            "format": "hidden",
                            "title": "",
                            "propertyOrder": 0,
                            "default": "true"
                          }
                       }
                    },
                    {
                      "title": "Add product to basket from Product list page",
                      "type": "object",
                      "description": "NOTE! 'Navigate to product list pages from main navigation' scenario should be enabled",
                      "properties": {
                        "plp": {
                          "type": "string",
                          "format": "hidden",
                          "title": "",
                          "propertyOrder": 0,
                          "default": "true"
                        }
                       }
                    }
                   ]
                 }
                }
              }, 
              {
                "title": "Do not run",
                "type": "object"
              }
            ]
          },
          "f_submitOrder":{
            "type": "object",
            "title": "Order submit",
            "propertyOrder": 6,
            "oneOf": [
              {
                "title": "Run scenario",
                "type": "object",
                "properties": {
                  "enabled": {
                    "type": "string",
                    "format": "hidden",
                    "title": "",
                    "propertyOrder": 0,
                    "default": "true"
                  },
                  "usersCount": {
                    "type": "string",
                    "title": "Users count",
                    "description": "Number of concurrent users submitting order",
                    "propertyOrder": 1,
                    "default": "${jsonConfig.order_usersCount}"
                  }
                }
              }, 
              {
                "title": "Do not run",
                "type": "object"
              }
            ]
          }
        }
      }
    }
  }
}/);