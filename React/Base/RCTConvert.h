/**
 * Copyright (c) 2015-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

#import <objc/message.h>

#import <QuartzCore/QuartzCore.h>
#import <UIKit/UIKit.h>

#import "../Layout/Layout.h"
#import "../Views/RCTAnimationType.h"
#import "../Views/RCTPointerEvents.h"

#import "RCTDefines.h"
#import "RCTLog.h"

/**
 * This class provides a collection of conversion functions for mapping
 * JSON objects to native types and classes. These are useful when writing
 * custom RCTViewManager setter methods.
 */
@interface RCTConvert : NSObject

+ (BOOL)BOOL:(id)json;
+ (double)double:(id)json;
+ (float)float:(id)json;
+ (int)int:(id)json;

+ (int64_t)int64_t:(id)json;
+ (uint64_t)uint64_t:(id)json;

+ (NSInteger)NSInteger:(id)json;
+ (NSUInteger)NSUInteger:(id)json;

+ (NSArray *)NSArray:(id)json;
+ (NSDictionary *)NSDictionary:(id)json;
+ (NSString *)NSString:(id)json;
+ (NSNumber *)NSNumber:(id)json;
+ (NSData *)NSData:(id)json;

+ (NSURL *)NSURL:(id)json;
+ (NSURLRequest *)NSURLRequest:(id)json;

+ (NSDate *)NSDate:(id)json;
+ (NSTimeZone *)NSTimeZone:(id)json;
+ (NSTimeInterval)NSTimeInterval:(id)json;

+ (NSTextAlignment)NSTextAlignment:(id)json;
+ (NSWritingDirection)NSWritingDirection:(id)json;
+ (UITextAutocapitalizationType)UITextAutocapitalizationType:(id)json;
+ (UITextFieldViewMode)UITextFieldViewMode:(id)json;
+ (UIScrollViewKeyboardDismissMode)UIScrollViewKeyboardDismissMode:(id)json;
+ (UIKeyboardType)UIKeyboardType:(id)json;
+ (UIReturnKeyType)UIReturnKeyType:(id)json;

+ (UIViewContentMode)UIViewContentMode:(id)json;
+ (UIBarStyle)UIBarStyle:(id)json;

+ (CGFloat)CGFloat:(id)json;
+ (CGPoint)CGPoint:(id)json;
+ (CGSize)CGSize:(id)json;
+ (CGRect)CGRect:(id)json;
+ (UIEdgeInsets)UIEdgeInsets:(id)json;

+ (CGLineCap)CGLineCap:(id)json;
+ (CGLineJoin)CGLineJoin:(id)json;

+ (CATransform3D)CATransform3D:(id)json;
+ (CGAffineTransform)CGAffineTransform:(id)json;

+ (UIColor *)UIColor:(id)json;
+ (CGColorRef)CGColor:(id)json;

+ (UIImage *)UIImage:(id)json;
+ (CGImageRef)CGImage:(id)json;

+ (UIFont *)UIFont:(UIFont *)font withSize:(id)json;
+ (UIFont *)UIFont:(UIFont *)font withWeight:(id)json;
+ (UIFont *)UIFont:(UIFont *)font withStyle:(id)json;
+ (UIFont *)UIFont:(UIFont *)font withFamily:(id)json;
+ (UIFont *)UIFont:(UIFont *)font withFamily:(id)family
              size:(id)size weight:(id)weight style:(id)style;

typedef NSArray NSStringArray;
+ (NSStringArray *)NSStringArray:(id)json;

typedef NSArray NSDictionaryArray;
+ (NSDictionaryArray *)NSDictionaryArray:(id)json;

typedef NSArray NSURLArray;
+ (NSURLArray *)NSURLArray:(id)json;

typedef NSArray NSNumberArray;
+ (NSNumberArray *)NSNumberArray:(id)json;

typedef NSArray UIColorArray;
+ (UIColorArray *)UIColorArray:(id)json;

typedef NSArray CGColorArray;
+ (CGColorArray *)CGColorArray:(id)json;

typedef BOOL css_overflow;
+ (css_overflow)css_overflow:(id)json;
+ (css_flex_direction_t)css_flex_direction_t:(id)json;
+ (css_justify_t)css_justify_t:(id)json;
+ (css_align_t)css_align_t:(id)json;
+ (css_position_type_t)css_position_type_t:(id)json;
+ (css_wrap_type_t)css_wrap_type_t:(id)json;

+ (RCTPointerEvents)RCTPointerEvents:(id)json;
+ (RCTAnimationType)RCTAnimationType:(id)json;

@end

/**
 * This function will attempt to set a property using a json value by first
 * inferring the correct type from all available information, and then
 * applying an appropriate conversion method. If the property does not
 * exist, or the type cannot be inferred, the function will return NO.
 */
RCT_EXTERN BOOL RCTSetProperty(id target, NSString *keyPath, SEL type, id json);

/**
 * This function attempts to copy a property from the source object to the
 * destination object using KVC. If the property does not exist, or cannot
 * be set, it will do nothing and return NO.
 */
RCT_EXTERN BOOL RCTCopyProperty(id target, id source, NSString *keyPath);

/**
 * Underlying implementations of RCT_XXX_CONVERTER macros. Ignore these.
 */
RCT_EXTERN NSNumber *RCTConvertEnumValue(const char *, NSDictionary *, NSNumber *, id);
RCT_EXTERN NSArray *RCTConvertArrayValue(SEL, id);
RCT_EXTERN void RCTLogConvertError(id, const char *);

/**
 * This macro is used for creating simple converter functions that just call
 * the specified getter method on the json value.
 */
#define RCT_CONVERTER(type, name, getter) \
RCT_CUSTOM_CONVERTER(type, name, [json getter])

/**
 * This macro is used for creating converter functions with arbitrary logic.
 */
#define RCT_CUSTOM_CONVERTER(type, name, code) \
+ (type)name:(id)json                          \
{                                              \
  json = (json == (id)kCFNull) ? nil : json;   \
  if (!RCT_DEBUG) {                            \
    return code;                               \
  } else {                                     \
    @try {                                     \
      return code;                             \
    }                                          \
    @catch (__unused NSException *e) {         \
      RCTLogConvertError(json, #type);         \
      json = nil;                              \
      return code;                             \
    }                                          \
  }                                            \
}

/**
 * This macro is similar to RCT_CONVERTER, but specifically geared towards
 * numeric types. It will handle string input correctly, and provides more
 * detailed error reporting if an invalid value is passed in.
 */
#define RCT_NUMBER_CONVERTER(type, getter) \
RCT_CUSTOM_CONVERTER(type, type, [[self NSNumber:json] getter])

/**
 * This macro is used for creating converters for enum types.
 */
#define RCT_ENUM_CONVERTER(type, values, default, getter) \
+ (type)type:(id)json                                     \
{                                                         \
  static NSDictionary *mapping;                           \
  static dispatch_once_t onceToken;                       \
  dispatch_once(&onceToken, ^{                            \
    mapping = values;                                     \
  });                                                     \
  NSNumber *converted = RCTConvertEnumValue(#type, mapping, @(default), json); \
  return ((type(*)(id, SEL))objc_msgSend)(converted, @selector(getter)); \
}

/**
 * This macro is used for creating converter functions for typed arrays.
 */
#define RCT_ARRAY_CONVERTER(type)                      \
+ (NSArray *)type##Array:(id)json                      \
{                                                      \
  return RCTConvertArrayValue(@selector(type:), json); \
}
